/**
 * ***********************************************************************
 *
 * ECHOBOX CONFIDENTIAL
 *
 * All Rights Reserved.
 *
 * NOTICE: All information contained herein is, and remains the property of
 * Echobox Ltd. and its
 * suppliers, if any. The intellectual and technical concepts contained
 * herein are proprietary to Echobox Ltd. and its suppliers and may be
 * covered by Patents, patents in process, and are protected by trade secret
 * or copyright law. Dissemination of this information or reproduction of
 * this material, in any format, is strictly forbidden unless prior written
 * permission is obtained from Echobox Ltd.
 */

import cdk = require("@aws-cdk/core");
import codepipeline = require("@aws-cdk/aws-codepipeline");
import codebuild = require("@aws-cdk/aws-codebuild");
import codepipelineactions = require("@aws-cdk/aws-codepipeline-actions");
import { IamPolicyGenerator } from "cdk-iam-generator";
import { Role, ServicePrincipal, PolicyStatement, Policy, ManagedPolicy } from "@aws-cdk/aws-iam";
import ec2 = require("@aws-cdk/aws-ec2");
import { Duration, RemovalPolicy, Construct } from "@aws-cdk/core";
import { Environment, ServiceBillingTags, ServiceType } from "./tags-util";
import { LogGroup, RetentionDays } from "@aws-cdk/aws-logs";
import { SecurityGroup } from "@aws-cdk/aws-ec2";
import { IBuildImage } from "@aws-cdk/aws-codebuild";
import { Topic } from "@aws-cdk/aws-sns";
import { EmailSubscription } from "@aws-cdk/aws-sns-subscriptions";
import { Rule } from "@aws-cdk/aws-events";
import { SnsTopic } from "@aws-cdk/aws-events-targets";
import s3 = require("@aws-cdk/aws-s3");

// Allows constructor to include the branch
interface MultiStackProps extends cdk.StackProps {
  owner?: string;
  repo?: string;
  branch?: string;
  service_name?: string;
  git_token_secret_arn?: string;
  git_connection_arn?: string;
  archiva_credentials_arn?: string;
  account_ref?: string;
  codebuild_vpc?: string;
  codebuild_az?: string;
  codebuild_private_subnet?: string;
  codebuild_timeout_mins?: number;
  create_webhook?: string;
  release_pipeline_branch_names?: string;
  build_instance_size?: string;
  aws_region?: string;
  build_container?: string;
  notification_email?: string;
  buildscripts_s3_bucket?: string;
  helm_charts_s3_bucket?: string;
}

export class ReleasePipelineNestedStack extends cdk.NestedStack {
  public constructor(scope: cdk.Construct, id: string, props?: MultiStackProps) {
    super(scope, id);

    let awsRegion = "";
    if (props && props.aws_region) {
      awsRegion = props.aws_region;
    }

    let repo = "";
    if (props && props.repo) {
      repo = props.repo;
    }

    let owner = "";
    if (props && props.owner) {
      owner = props.owner;
    }

    let serviceName = "";
    if (props && props.service_name) {
      serviceName = props.service_name;
    }

    let gitTokenSecretArn = "";
    if (props && props.git_token_secret_arn) {
      gitTokenSecretArn = props.git_token_secret_arn;
    }

    let githubConnectionArn = "";
    if (props && props.git_connection_arn) {
      githubConnectionArn = props.git_connection_arn;
    }

    let archivaCredentialsArn = "";
    if (props && props.archiva_credentials_arn) {
      archivaCredentialsArn = props.archiva_credentials_arn;
    }

    let accountRef = "";
    if (props && props.account_ref) {
      accountRef = props.account_ref;
    }

    let vpcForCodebuild = "";
    if (props && props.codebuild_vpc) {
      vpcForCodebuild = props.codebuild_vpc;
    }

    let azForCodebuild = "";
    if (props && props.codebuild_az) {
      azForCodebuild = props.codebuild_az;
    }

    let privateSubnetForCodebuild = "";
    if (props && props.codebuild_private_subnet) {
      privateSubnetForCodebuild = props.codebuild_private_subnet;
    }

    let codebuildTimeoutMins = 0;
    if (props && props.codebuild_timeout_mins) {
      codebuildTimeoutMins = Number(props.codebuild_timeout_mins);
    }

    let releasePipelineBranchNames = "";
    if (props && props.release_pipeline_branch_names) {
      releasePipelineBranchNames = props.release_pipeline_branch_names;
    }

    // load instance type size
    let buildInstanceSize = "";
    if (props && props.build_instance_size) {
      buildInstanceSize = props.build_instance_size;
    }

    let buildContainer = true;
    if (props && props.build_container) {
      if (props.build_container == "false") {
        buildContainer = false;
      }
    }

    let notificationEmail = "";
    if (props && props.notification_email) {
      notificationEmail = props.notification_email;
    }

    let buildscriptsS3Bucket = "";
    if (props && props.buildscripts_s3_bucket) {
      buildscriptsS3Bucket = props.buildscripts_s3_bucket;
    }

    let helmChartsS3Bucket = "";
    if (props && props.helm_charts_s3_bucket) {
      helmChartsS3Bucket = props.helm_charts_s3_bucket;
    }

    // If build_container is true it means the pipeline and codebuild will be building containers
    // The pipeline will need to run in Privileged mode
    const isPrivileged = buildContainer;

    // loop through the branches building the pipelines
    const branches = releasePipelineBranchNames.split(",");
    for (let i = 0; i < branches.length; i++) {
      this.buildPipeline(
        scope,
        awsRegion,
        accountRef,
        serviceName,
        vpcForCodebuild,
        azForCodebuild,
        privateSubnetForCodebuild,
        codebuildTimeoutMins,
        repo,
        owner,
        gitTokenSecretArn,
        githubConnectionArn,
        archivaCredentialsArn,
        branches[i],
        buildInstanceSize,
        isPrivileged,
        notificationEmail,
        buildscriptsS3Bucket,
        helmChartsS3Bucket
      );
    }
  }

  public buildPipeline(
    scope: cdk.Construct,
    awsRegion: string,
    accountRef: string,
    serviceName: string,
    vpcForCodebuild: string,
    azForCodebuild: string,
    privateSubnetForCodebuild: string,
    codebuildTimeoutMins: number,
    repo: string,
    owner: string,
    gitTokenSecretArn: string,
    githubConnectionArn: string,
    archivaCredentialsArn: string,
    branch: string,
    buildInstanceSize: string,
    isPrivileged: boolean,
    notificationEmail: string,
    buildscriptsS3Bucket: string,
    helmChartsS3Bucket: string
  ): void {
    const serviceNameTag =
      owner + "-" + repo + serviceName + "-Service-Stack-CodePipeline-" + branch;

    const ENVIRONMENT_TAG = Environment.Production;
    const SERVICE_TYPE_TAG = ServiceType.CI;

    const BUILD_IMAGE = codebuild.LinuxBuildImage.STANDARD_4_0;

    // make the spec branch dependent
    const BUILD_SPEC = "buildspec-release-" + branch + ".yml";

    // naming of pipeline based upon Branch
    const PIPELINE_NAME = "Pipe-" + owner + "-" + repo + "-" + branch;

    // naming of CodeBuild based upon Branch
    const CODEBUILD_PROJECT_NAME = PIPELINE_NAME + "-CodeBuild";

    const ROLE_NAME = `${CODEBUILD_PROJECT_NAME}-Role`;
    const SECURITY_GROUP_NAME = `${CODEBUILD_PROJECT_NAME}-SG`;
    const PIPELINE_ARTIFACT_BUCKET_NAME = "echobox-" + CODEBUILD_PROJECT_NAME.toLowerCase();

    // bucket for pipeline artifacts
    //const key = new kms.Key(this, 'Key');
    //key.addAlias('alias/testkey');
    const pipelineArtifactBucket = new s3.Bucket(this, PIPELINE_ARTIFACT_BUCKET_NAME, {
      //encryptionKey: key,
      encryption: s3.BucketEncryption.UNENCRYPTED,
      bucketName: PIPELINE_ARTIFACT_BUCKET_NAME,
      removalPolicy: RemovalPolicy.DESTROY
    });

    // Tag the s3 bucket
    ServiceBillingTags.setEnvironment(pipelineArtifactBucket, ENVIRONMENT_TAG);
    ServiceBillingTags.setServiceType(pipelineArtifactBucket, ServiceType.S3);
    ServiceBillingTags.setServiceName(pipelineArtifactBucket, serviceName);

    // get value from AWS Secret Manager
    let SECRET_MANAGER_GIT_KEY_ARN = "";

    // If a token is provided seperately then use this instead
    // This feature is most likely to be used if associating pipeline with non ebx repo
    if (gitTokenSecretArn != "") {
      SECRET_MANAGER_GIT_KEY_ARN = gitTokenSecretArn;
    }

    const GITHUB_CONNECTION_ARN = githubConnectionArn;

    // set the readme.io token arn
    // prettier-ignore
    const README_TOKEN_ARN =
      `arn:aws:ssm:${awsRegion}:${accountRef}:parameter/Prod/External/Readme.io/APIToken`;

    // refernce to SSM param that stored EKS cluster to deploy changes to
    // prettier-ignore
    const EKS_CLUSTER_NAME_SSM_ARN =
      `arn:aws:ssm:${awsRegion}:${accountRef}:parameter/Infrastructure/EKS/ClusterName`;

    const sourceOutput = new codepipeline.Artifact();

    const sourceAction = ReleasePipelineNestedStack.createSourceAction(
      owner,
      repo,
      branch,
      GITHUB_CONNECTION_ARN,
      sourceOutput
    );

    const vpc = ec2.Vpc.fromVpcAttributes(
      this,
      vpcForCodebuild + "-" + owner + "-" + repo + "-" + branch,
      {
        vpcId: vpcForCodebuild,
        availabilityZones: [azForCodebuild],
        privateSubnetIds: [privateSubnetForCodebuild]
      }
    );

    // role per codebuild project
    const buildRole = new Role(this, ROLE_NAME, {
      roleName: ROLE_NAME,
      assumedBy: new ServicePrincipal("codebuild.amazonaws.com"),
      path: "/"
    });

    // Only handle addtional managed policy if this is for master branch
    // not expected that dev pipeline would ever build infrstructure via CDK
    // now create the policy from JSON
    if (branch == "master") {
      new IamPolicyGenerator(this, "IamPolicyGenerator", {
        configPath: "config/iam_generator_config.json",
        policyPath: "config/policy"
      });

      // assign the policy to the role
      const managedPolicyName = "Echobox-PipelinePolicy-" + serviceName;
      const managedPolicy = ManagedPolicy.fromManagedPolicyName(
        this,
        managedPolicyName,
        managedPolicyName
      );
      buildRole.addManagedPolicy(managedPolicy);
    }

    // create security group - one per codebuild project
    const securityGroup = new ec2.SecurityGroup(this, SECURITY_GROUP_NAME, {
      description: `Security Group for codebuild ${`${CODEBUILD_PROJECT_NAME}`}`,
      securityGroupName: SECURITY_GROUP_NAME,
      vpc,
      allowAllOutbound: true
    });

    const codeBuildProject = ReleasePipelineNestedStack.createCodeBuildProject(
      this,
      CODEBUILD_PROJECT_NAME,
      repo,
      branch,
      buildRole,
      vpc,
      securityGroup,
      codebuildTimeoutMins,
      buildInstanceSize,
      BUILD_IMAGE,
      BUILD_SPEC,
      isPrivileged
    );

    // set permission for secret manager
    ReleasePipelineNestedStack.attachSecretServiceAccessPolicy(
      this,
      buildRole,
      serviceNameTag,
      SECRET_MANAGER_GIT_KEY_ARN
    );

    ReleasePipelineNestedStack.attachReadmeAPIPolicy(
      this,
      buildRole,
      serviceNameTag,
      README_TOKEN_ARN
    );

    // allow role to stop builds of the same project
    // prettier-ignore
    const BUILD_CTRL_ARN =
      `arn:aws:codebuild:${awsRegion}:${accountRef}:project/${CODEBUILD_PROJECT_NAME}`;
    ReleasePipelineNestedStack.attachBuildControlPolicy(
      this,
      buildRole,
      serviceNameTag,
      BUILD_CTRL_ARN
    );

    // permission to send email
    const SEND_EMAIL_ARN = `arn:aws:ses:${awsRegion}:${accountRef}:identity/*@echobox.com`;
    ReleasePipelineNestedStack.attachSendEmailPolicy(
      this,
      buildRole,
      serviceNameTag,
      SEND_EMAIL_ARN
    );

    // Add topic notification
    ReleasePipelineNestedStack.attachTopicNotification(this, PIPELINE_NAME, notificationEmail);

    // Add ability to get pipeline state
    const PIPELINE_ARN = `arn:aws:codepipeline:${awsRegion}:${accountRef}:${PIPELINE_NAME}`;
    ReleasePipelineNestedStack.attachGetPipelineStatePolicy(
      this,
      buildRole,
      serviceNameTag,
      PIPELINE_ARN
    );

    // send email at end of build requires access to pipelines
    const LIST_PIPELINES_ARN = `arn:aws:codepipeline:${awsRegion}:${accountRef}:*`;
    ReleasePipelineNestedStack.attachListPipelinesPolicy(
      this,
      buildRole,
      serviceNameTag,
      LIST_PIPELINES_ARN
    );

    const BUILDSCRIPTS_ARN =
      `arn:aws:ssm:${awsRegion}:${accountRef}` +
      `:parameter/Infrastructure/Codebuild/DefaultBuildScriptsLocation`;
    ReleasePipelineNestedStack.attachBuildscriptSsmPolicy(
      this,
      buildRole,
      serviceNameTag,
      BUILDSCRIPTS_ARN
    );

    // prettier-ignore
    const CLOUDFORMATION_ARN =
      `arn:aws:cloudformation:${awsRegion}:${accountRef}:stack/Infrastructure/*`;
    ReleasePipelineNestedStack.attachCloudFormationDescribeStacksPolicy(
      this,
      buildRole,
      serviceNameTag,
      CLOUDFORMATION_ARN
    );

    // If The repo does not need to build containers then many of the following permissions
    // are not required.
    if (isPrivileged == true) {
      ReleasePipelineNestedStack.attachEKSNamePolicy(
        this,
        buildRole,
        serviceNameTag,
        EKS_CLUSTER_NAME_SSM_ARN
      );

      // get the arn for the EKS cluster
      const EKS_CLUSTER_NAME_ARN = `arn:aws:eks:${awsRegion}:${accountRef}:cluster/EchoboxEKS*`;
      ReleasePipelineNestedStack.attachEKSDescribePolicy(
        this,
        buildRole,
        serviceNameTag,
        EKS_CLUSTER_NAME_ARN
      );

      // allow build to get token to run docker login on ECR repo
      ReleasePipelineNestedStack.attachECRLoginPolicy(this, buildRole, serviceNameTag);

      const ECR_ACCESS_ARN = `arn:aws:ecr:${awsRegion}:${accountRef}:repository/${owner}/${repo}/*`;
      const ECR_ACCESS_ALL_ARN = `arn:aws:ecr:${awsRegion}:${accountRef}:repository/*`;
      ReleasePipelineNestedStack.attachECRPermissionsPolicy(
        this,
        buildRole,
        serviceNameTag,
        ECR_ACCESS_ARN,
        ECR_ACCESS_ALL_ARN
      );

      // Permission to Helm Charts stored on s3
      const HELM_S3_ACCESS_ARN = `arn:aws:s3:::${helmChartsS3Bucket}`;
      const HELM_S3_PATH = `charts`;
      const HELM_S3_HELMFILE_PATH = `deployed_helmfiles`;
      ReleasePipelineNestedStack.attachHelmS3AccessPolicy(
        this,
        buildRole,
        serviceNameTag,
        HELM_S3_ACCESS_ARN,
        HELM_S3_PATH,
        HELM_S3_HELMFILE_PATH
      );
    }

    const BUILDSCRIPTS_S3_ACCESS_ARN = `arn:aws:s3:::${buildscriptsS3Bucket}`;
    const BUILDSCRIPTS_S3_PATH = ``;
    ReleasePipelineNestedStack.attachBuildscriptsS3AccessPolicy(
      this,
      buildRole,
      serviceNameTag,
      BUILDSCRIPTS_S3_ACCESS_ARN,
      BUILDSCRIPTS_S3_PATH
    );

    // prettier-ignore
    const BUILD_EMAIL_ARN =
      `arn:aws:ssm:${awsRegion}:${accountRef}:parameter/Prod/Internal/Build-Email`;
    ReleasePipelineNestedStack.attachBuildEmailParamAccessPolicy(
      this,
      buildRole,
      serviceNameTag,
      BUILD_EMAIL_ARN
    );

    const ECHOBOX_EMAIL_ARN = `arn:aws:ses:eu-west-1:558091818291:identity/*echobox.com`;
    ReleasePipelineNestedStack.attachEchoboxSendEmailPolicy(
      this,
      buildRole,
      serviceNameTag,
      ECHOBOX_EMAIL_ARN
    );

    const ARCHIVA_CREDENTIALS_ARN = archivaCredentialsArn;
    ReleasePipelineNestedStack.attachArchivaCredentialsSecretPolicy(
      this,
      buildRole,
      serviceNameTag,
      ARCHIVA_CREDENTIALS_ARN
    );

    // tag the pipeline
    ServiceBillingTags.setEnvironment(codeBuildProject, ENVIRONMENT_TAG);
    ServiceBillingTags.setServiceType(codeBuildProject, SERVICE_TYPE_TAG);
    ServiceBillingTags.setServiceName(codeBuildProject, serviceName);

    const buildAction = new codepipelineactions.CodeBuildAction({
      actionName: "Build",
      input: sourceOutput,
      project: codeBuildProject
    });

    const pipeline = new codepipeline.Pipeline(this, PIPELINE_NAME, {
      artifactBucket: pipelineArtifactBucket,
      pipelineName: PIPELINE_NAME
    });

    // tag the project
    ServiceBillingTags.setEnvironment(pipeline, ENVIRONMENT_TAG);
    ServiceBillingTags.setServiceType(pipeline, SERVICE_TYPE_TAG);
    ServiceBillingTags.setServiceName(pipeline, serviceName);

    pipeline.addStage({
      stageName: "Source",
      actions: [sourceAction]
    });

    pipeline.addStage({
      stageName: "Build",
      actions: [buildAction]
    });

    // create Log group
    // Note:  The CodeBuild project will log to the group name derived from the project name
    // therefore the LogGroup MUST be named in the same way otherwise it will log elsewhere
    // configured to delete if the stack is deleted
    new LogGroup(this, CODEBUILD_PROJECT_NAME + "-LogGroup", {
      logGroupName: "/aws/codebuild/" + CODEBUILD_PROJECT_NAME,
      retention: RetentionDays.ONE_MONTH,
      removalPolicy: RemovalPolicy.DESTROY
    });
  }

  public static attachBuildControlPolicy(
    construct: Construct,
    buildRole: Role,
    serviceNameTag: string,
    buildControlArn: string
  ): void {
    // inline policy to allow role to stop builds of the same codebuild project
    const policyStatement = new PolicyStatement();
    const policy = new Policy(construct, `${serviceNameTag}-StopBuilds`, {});
    policyStatement.addActions("codebuild:StopBuild");
    policyStatement.addActions("codebuild:StartBuild");
    policyStatement.addActions("codebuild:BatchGetBuilds");
    policyStatement.addActions("codebuild:ListBuildsForProject");
    policyStatement.addResources(buildControlArn);
    policy.addStatements(policyStatement);
    buildRole.attachInlinePolicy(policy);
  }

  public static attachEKSNamePolicy(
    construct: Construct,
    buildRole: Role,
    serviceNameTag: string,
    eksNameArn: string
  ): void {
    // inline policy to allow sending email - used for notifications etc
    const policyStatement = new PolicyStatement();
    const policy = new Policy(construct, `${serviceNameTag}-EksName`, {});
    policyStatement.addActions("ssm:GetParameters");
    policyStatement.addResources(eksNameArn);
    policy.addStatements(policyStatement);
    buildRole.attachInlinePolicy(policy);
  }

  public static attachSecretServiceAccessPolicy(
    construct: Construct,
    buildRole: Role,
    serviceNameTag: string,
    SecretAccessTokenArn: string
  ): void {
    // build inline policy to allow access to stored parameter holding git token
    // note that wildcard * added to end of arn as there is a 6 character random suffix
    // https://docs.aws.amazon.com/secretsmanager/latest/apireference/API_GetResourcePolicy.html
    const policyStatement = new PolicyStatement();
    const policy = new Policy(construct, `${serviceNameTag}-Secret-SecretService-Access`, {});
    policyStatement.addActions("secretsmanager:GetSecretValue");
    policyStatement.addResources(SecretAccessTokenArn + "*");
    policy.addStatements(policyStatement);
    buildRole.attachInlinePolicy(policy);
  }

  public static attachHelmS3AccessPolicy(
    construct: Construct,
    buildRole: Role,
    serviceNameTag: string,
    helmS3Arn: string,
    helmS3Path: string,
    helmfileS3Path: string
  ): void {
    // build inline policy to build to access s3 bucket where helm packages stored
    // The HelmS3Arn is the S3 bucket that stores the packages
    // The HelmS3Path is the path from root - usually "charts"
    const policyStatement1 = new PolicyStatement();
    const policyStatement2 = new PolicyStatement();
    const policy = new Policy(construct, `${serviceNameTag}-Helm-S3-Access`, {});
    policyStatement1.addActions("s3:PutObjectAcl");
    policyStatement1.addActions("s3:PutObject");
    policyStatement1.addActions("s3:GetObjectAcl");
    policyStatement1.addActions("s3:GetObject");
    policyStatement1.addActions("s3:DeleteObject");
    policyStatement1.addResources(helmS3Arn + "/" + helmS3Path);
    policyStatement1.addResources(helmS3Arn + "/" + helmS3Path + "/*");
    policyStatement1.addResources(helmS3Arn + "/" + helmfileS3Path);
    policyStatement1.addResources(helmS3Arn + "/" + helmfileS3Path + "/*");
    policy.addStatements(policyStatement1);
    policyStatement2.addActions("s3:ListBucket");
    policyStatement2.addResources(helmS3Arn);
    policy.addStatements(policyStatement2);
    buildRole.attachInlinePolicy(policy);
  }

  public static attachBuildscriptSsmPolicy(
    construct: Construct,
    buildRole: Role,
    serviceNameTag: string,
    buildscriptsSsmArn: string
  ): void {
    const policyStatement = new PolicyStatement();
    const policy = new Policy(construct, `${serviceNameTag}-Buildscripts-SSM`, {});
    policyStatement.addActions("ssm:GetParameter");
    policyStatement.addResources(buildscriptsSsmArn);
    policy.addStatements(policyStatement);
    buildRole.attachInlinePolicy(policy);
  }

  public static attachBuildscriptsS3AccessPolicy(
    construct: Construct,
    buildRole: Role,
    serviceNameTag: string,
    buildscriptsS3Arn: string,
    buildscriptsS3Path: string
  ): void {
    // build inline policy to allow access to Buildscripts archive on s3
    const policyStatement1 = new PolicyStatement();
    const policyStatement2 = new PolicyStatement();
    const policy = new Policy(construct, `${serviceNameTag}-Buildscripts-S3-Access`, {});
    policyStatement1.addActions("s3:PutObjectAcl");
    policyStatement1.addActions("s3:PutObject");
    policyStatement1.addActions("s3:GetObjectAcl");
    policyStatement1.addActions("s3:GetObject");
    policyStatement1.addActions("s3:DeleteObject");
    if (buildscriptsS3Path) {
      buildscriptsS3Path = "/" + buildscriptsS3Path;
    }
    policyStatement1.addResources(buildscriptsS3Arn + buildscriptsS3Path);
    policyStatement1.addResources(buildscriptsS3Arn + buildscriptsS3Path + "/*");
    policy.addStatements(policyStatement1);
    policyStatement2.addActions("s3:ListBucket");
    policyStatement2.addResources(buildscriptsS3Arn);
    policyStatement2.addResources(buildscriptsS3Arn + "/*");
    policy.addStatements(policyStatement2);
    buildRole.attachInlinePolicy(policy);
  }

  public static attachReadmeAPIPolicy(
    construct: Construct,
    buildRole: Role,
    serviceNameTag: string,
    readmeDocumentTokenArn: string
  ): void {
    const policyStatement = new PolicyStatement();
    const policy = new Policy(construct, `${serviceNameTag}-ParameterStore-ReadmeToken`, {});
    policyStatement.addActions("ssm:GetParameters");
    policyStatement.addResources(readmeDocumentTokenArn);
    policy.addStatements(policyStatement);
    buildRole.attachInlinePolicy(policy);
  }

  public static attachSendEmailPolicy(
    construct: Construct,
    buildRole: Role,
    serviceNameTag: string,
    sendEmailArn: string
  ): void {
    // inline policy to allow sending email - used for notifications etc
    const policyStatement = new PolicyStatement();
    const policy = new Policy(construct, `${serviceNameTag}-SendEmails`, {});
    policyStatement.addActions("ses:SendEmail");
    policyStatement.addResources(sendEmailArn);
    policy.addStatements(policyStatement);
    buildRole.attachInlinePolicy(policy);
  }

  public static attachTopicNotification(
    construct: Construct,
    pipelineName: string,
    notificationEmail: string
  ): void {
    const topic = new Topic(construct, pipelineName + "-Topic", {
      displayName: "Notification topic for pipeline " + pipelineName
    });

    topic.addSubscription(new EmailSubscription(notificationEmail));

    const rule = new Rule(construct, pipelineName + "-notification-rule");

    rule.addTarget(new SnsTopic(topic));
    rule.addEventPattern({
      source: ["aws.codepipeline"],
      detailType: ["CodePipeline Pipeline Execution State Change"],
      detail: {
        state: ["FAILED"],
        pipeline: [pipelineName]
      }
    });
  }

  public static attachGetPipelineStatePolicy(
    construct: Construct,
    buildRole: Role,
    serviceNameTag: string,
    pipelineArn: string
  ): void {
    // inline policy to allow sending email - used for notifications etc
    const policyStatement = new PolicyStatement();
    const policy = new Policy(construct, `${serviceNameTag}-PipelineState`, {});
    policyStatement.addActions("codepipeline:GetPipelineState");
    policyStatement.addResources(pipelineArn);
    policy.addStatements(policyStatement);
    buildRole.attachInlinePolicy(policy);
  }

  public static attachListPipelinesPolicy(
    construct: Construct,
    buildRole: Role,
    serviceNameTag: string,
    listPipelinesArn: string
  ): void {
    // inline policy to allow access to pipeline info
    const policyStatement = new PolicyStatement();
    const policy = new Policy(construct, `${serviceNameTag}-ListPipelines`, {});
    policyStatement.addActions("codepipeline:ListPipelines");
    policyStatement.addActions("codepipeline:ListPipelineExecutions");
    policyStatement.addActions("codepipeline:GetPipeline");
    policyStatement.addActions("codepipeline:GetPipelineExecution");
    policyStatement.addResources(listPipelinesArn);
    policy.addStatements(policyStatement);
    buildRole.attachInlinePolicy(policy);
  }

  public static attachECRLoginPolicy(
    construct: Construct,
    buildRole: Role,
    serviceNameTag: string
  ): void {
    // build inline policy to allow access to stored parameter holding git token
    const policyStatement = new PolicyStatement();
    const policy = new Policy(construct, `${serviceNameTag}-ECR-Login-Access`, {});
    policyStatement.addActions("ecr:GetAuthorizationToken");
    policyStatement.addResources("*");
    policy.addStatements(policyStatement);
    buildRole.attachInlinePolicy(policy);
  }

  public static attachEKSDescribePolicy(
    construct: Construct,
    buildRole: Role,
    serviceNameTag: string,
    eksClusterArn: string
  ): void {
    // build inline policy to allow access to stored parameter holding git token
    const policyStatement = new PolicyStatement();
    const policy = new Policy(construct, `${serviceNameTag}-EKS-Describe`, {});
    policyStatement.addActions("eks:DescribeCluster");
    policyStatement.addResources(eksClusterArn);
    policy.addStatements(policyStatement);
    buildRole.attachInlinePolicy(policy);
  }

  public static attachECRPermissionsPolicy(
    construct: Construct,
    buildRole: Role,
    serviceNameTag: string,
    ecrAccessArn: string,
    ecrAccessAllArn: string
  ): void {
    // build inline policy to allow access to stored parameter holding git token
    const policyStatement = new PolicyStatement();
    const policy = new Policy(construct, `${serviceNameTag}-ECR-Permissions-Access`, {});
    policyStatement.addActions("ecr:PutLifecyclePolicy");
    policyStatement.addActions("ecr:CreateRepository");
    policyStatement.addActions("ecr:SetRepositoryPolicy");
    policyStatement.addActions("ecr:DescribeRepositories");
    policyStatement.addActions("ecr:BatchGetImage");
    policyStatement.addActions("ecr:PutImage");
    policyStatement.addActions("ecr:InitiateLayerUpload");
    policyStatement.addActions("ecr:TagResource");
    policyStatement.addActions("ecr:ListImages");
    policyStatement.addResources(ecrAccessArn);
    policy.addStatements(policyStatement);
    const policyStatement2 = new PolicyStatement();
    policyStatement2.addActions("ecr:DescribeRepositories");
    policyStatement2.addResources(ecrAccessAllArn);
    policy.addStatements(policyStatement2);
    buildRole.attachInlinePolicy(policy);
  }

  public static createSourceAction(
    owner: string,
    repository: string,
    branch: string,
    githubConnection: string,
    sourceOutput: codepipeline.Artifact
  ): codepipelineactions.BitBucketSourceAction {
    const sourceAction = new codepipelineactions.BitBucketSourceAction({
      actionName: "Source",
      owner: owner,
      repo: repository,
      output: sourceOutput,
      branch: branch,
      connectionArn: githubConnection
    });

    return sourceAction;
  }

  public static createBuildAction(
    sourceInput: codepipeline.Artifact,
    project: codebuild.PipelineProject
  ): codepipelineactions.CodeBuildAction {
    const buildAction = new codepipelineactions.CodeBuildAction({
      actionName: "Build",
      input: sourceInput,
      project
    });
    return buildAction;
  }

  public static attachCloudFormationDescribeStacksPolicy(
    construct: Construct,
    buildRole: Role,
    serviceNameTag: string,
    cloudFormationArn: string
  ): void {
    // inline policy to allow read access to stacks
    const policyStatement = new PolicyStatement();
    const policy = new Policy(construct, `${serviceNameTag}-CloudFormation`, {});
    policyStatement.addActions("cloudformation:DescribeStacks");
    policyStatement.addResources(cloudFormationArn);
    policy.addStatements(policyStatement);
    buildRole.attachInlinePolicy(policy);
  }

  public static attachBuildEmailParamAccessPolicy(
    construct: Construct,
    buildRole: Role,
    serviceNameTag: string,
    buildEmailArn: string
  ): void {
    const policyStatement = new PolicyStatement();
    const policy = new Policy(construct, `${serviceNameTag}-BuildEmail`, {});
    policyStatement.addActions("ssm:GetParameters");
    policyStatement.addResources(buildEmailArn);
    policy.addStatements(policyStatement);
    buildRole.attachInlinePolicy(policy);
  }

  public static attachEchoboxSendEmailPolicy(
    construct: Construct,
    buildRole: Role,
    serviceNameTag: string,
    sesIdentityArn: string
  ): void {
    const policy = new Policy(construct, `${serviceNameTag}-EchoboxEmail`, {});
    const policyStatement = new PolicyStatement();
    policyStatement.addActions("ses:SendEmail");
    policyStatement.addResources(sesIdentityArn);
    policy.addStatements(policyStatement);
    buildRole.attachInlinePolicy(policy);
  }

  public static attachArchivaCredentialsSecretPolicy(
    construct: Construct,
    buildRole: Role,
    serviceNameTag: string,
    archivaCredSecretArn: string
  ): void {
    const policy = new Policy(construct, `${serviceNameTag}-ArcivaCredentials`, {});
    const policyStatement = new PolicyStatement();
    policyStatement.addActions("secretsmanager:GetSecretValue");
    policyStatement.addResources(archivaCredSecretArn);
    policy.addStatements(policyStatement);
    buildRole.attachInlinePolicy(policy);
  }

  public static createCodeBuildProject(
    construct: Construct,
    projectName: string,
    repository: string,
    branch: string,
    buildRole: Role,
    vpc: ec2.IVpc,
    securityGroup: SecurityGroup,
    codebuildTimeoutMins: number,
    buildInstanceSize: string,
    buildImage: IBuildImage,
    buildSpec: string,
    isPrivileged: boolean
  ): codebuild.PipelineProject {
    // note: the codebuild environment spec and pricing  can be found at
    // https://aws.amazon.com/codebuild/pricing/
    let computeType = codebuild.ComputeType.SMALL;
    switch (buildInstanceSize) {
      case "SMALL": {
        computeType = codebuild.ComputeType.SMALL;
        break;
      }
      case "MEDIUM": {
        computeType = codebuild.ComputeType.MEDIUM;
        break;
      }
      case "LARGE": {
        computeType = codebuild.ComputeType.LARGE;
        break;
      }
      case "XLARGE": {
        computeType = codebuild.ComputeType.X2_LARGE;
        break;
      }
    }

    const project = new codebuild.PipelineProject(construct, projectName, {
      // render url for build badge
      description:
        `An AWS codebuild project (part of pipeline)` +
        ` for repo: ${repository}, branch: ${branch} created: ${Date()}`,
      // note will use standard buildspec.yml
      // set the environment
      environment: {
        computeType: computeType,
        buildImage: buildImage,
        privileged: isPrivileged
      },
      projectName: projectName,
      role: buildRole,
      // The number of minutes after which AWS CodeBuild stops the build if it's not complete
      timeout: Duration.minutes(codebuildTimeoutMins),
      vpc: vpc,
      securityGroups: [securityGroup],
      // This is using a different yml file as the steps are different between PR builds
      // and release builds.
      buildSpec: codebuild.BuildSpec.fromSourceFilename(buildSpec)
    });

    return project;
  }
}
