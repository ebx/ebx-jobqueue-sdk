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

import { Role, ServicePrincipal, PolicyStatement, Policy } from "@aws-cdk/aws-iam";
import { Environment, ServiceBillingTags, ServiceType } from "./tags-util";

import codebuild = require("@aws-cdk/aws-codebuild");
import cdk = require("@aws-cdk/core");
import ec2 = require("@aws-cdk/aws-ec2");
import { LogGroup, RetentionDays } from "@aws-cdk/aws-logs";
import { Duration, RemovalPolicy, Construct } from "@aws-cdk/core";

// Allows constructor to include the branch
interface MultiStackProps extends cdk.StackProps {
  owner?: string;
  repo?: string;
  git_token_secret_arn?: string;
  git_connection_arn?: string;
  account_ref?: string;
  codebuild_vpc?: string;
  codebuild_az?: string;
  codebuild_private_subnet?: string;
  codebuild_timeout_mins?: number;
  service_name?: string;
  create_webhook?: string;
  build_instance_size?: string;
  aws_region?: string;
  build_container?: string;
  buildscripts_s3_bucket?: string;
  helm_charts_s3_bucket?: string;
}

export class PRBuildStack extends cdk.NestedStack {
  // What billing tags should be used?
  public static readonly ENVIRONMENT_TAG = Environment.Production;
  public static readonly SERVICE_TYPE_TAG = ServiceType.CI;

  // should be true if need elevated privileges - required to build docker containers
  public static readonly PRIVILEGED = true;

  // note: when selecting a standard image need to specify packages in buildspec - see TEC-7667
  public static readonly CODEBUILD_IMAGE = codebuild.LinuxBuildImage.STANDARD_4_0;

  public constructor(scope: cdk.Construct, id: string, props?: MultiStackProps) {
    super(scope, id);

    // load instance type size
    let build_instance_size = "";
    if (props && props.build_instance_size) {
      build_instance_size = props.build_instance_size;
    }

    // note: the codebuild environment spec and pricing  can be found at
    // https://aws.amazon.com/codebuild/pricing/
    let computeType = codebuild.ComputeType.SMALL;
    switch (build_instance_size) {
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

    // load setting from properties
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

    let gitTokenSecretArn = "";
    if (props && props.git_token_secret_arn) {
      gitTokenSecretArn = props.git_token_secret_arn;
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

    let serviceName = "";
    if (props && props.service_name) {
      serviceName = props.service_name;
    }

    let createWebhook = true;
    if (props && props.create_webhook) {
      if (props.create_webhook == "false") {
        createWebhook = false;
      }
    }

    let buildContainer = true;
    if (props && props.build_container) {
      if (props.build_container == "false") {
        buildContainer = false;
      }
    }

    let codebuildTimeoutMins = 0;
    if (props && props.codebuild_timeout_mins) {
      codebuildTimeoutMins = Number(props.codebuild_timeout_mins);
    }

    let buildscriptsS3Bucket = "";
    if (props && props.buildscripts_s3_bucket) {
      buildscriptsS3Bucket = props.buildscripts_s3_bucket;
    }

    let helmChartsS3Bucket = "";
    if (props && props.helm_charts_s3_bucket) {
      helmChartsS3Bucket = props.helm_charts_s3_bucket;
    }

    const serviceNameTag = owner + "-" + repo + "-" + serviceName;

    // This will be the set to the stack name for convenience
    const CODEBUILD_PROJECT_NAME = `CodeBuild-PR-${owner}-${repo}`;

    // If build_container is true it means the pipeline and codebuild will be building containers
    // The pipeline will need to run in Privileged mode
    const isPrivileged = buildContainer;

    // import the VPC
    // availablity zone and subnet required when there is more than one av for the VPV
    const vpc = ec2.Vpc.fromVpcAttributes(this, vpcForCodebuild, {
      vpcId: vpcForCodebuild,
      availabilityZones: [azForCodebuild],
      privateSubnetIds: [privateSubnetForCodebuild]
    });

    // create security group - one per codebuild project
    // security group
    const SECURITY_GROUP_NAME = `${CODEBUILD_PROJECT_NAME}-SG`;
    const securityGroup = new ec2.SecurityGroup(this, SECURITY_GROUP_NAME, {
      description: `Security Group for codebuild ${`${CODEBUILD_PROJECT_NAME}`}`,
      securityGroupName: SECURITY_GROUP_NAME,
      vpc,
      allowAllOutbound: true
    });

    const webhookFilters = [
      codebuild.FilterGroup.inEventOf(codebuild.EventAction.PULL_REQUEST_CREATED),
      codebuild.FilterGroup.inEventOf(codebuild.EventAction.PULL_REQUEST_UPDATED),
      codebuild.FilterGroup.inEventOf(codebuild.EventAction.PULL_REQUEST_REOPENED)
    ];

    // set the filters if webhooks specified
    const sourceProperties = {
      owner: owner,
      repo: repo,
      reportBuildStatus: true,
      webhook: createWebhook,
      webhookFilters: createWebhook ? webhookFilters : []
    };

    const source = codebuild.Source.gitHub(sourceProperties);

    // role per codebuild project
    const ROLE_NAME = `${CODEBUILD_PROJECT_NAME}-Role`;
    const buildRole = new Role(this, ROLE_NAME, {
      roleName: ROLE_NAME,
      assumedBy: new ServicePrincipal("codebuild.amazonaws.com"),
      path: "/"
    });

    // get value from AWS Secret Manager
    // If other secret been passed in then use that insteadS
    const SECRET_MANAGER_GIT_KEY_ARN = gitTokenSecretArn;

    PRBuildStack.attachSecretManagerAccessPolicy(
      this,
      buildRole,
      serviceNameTag,
      SECRET_MANAGER_GIT_KEY_ARN
    );

    // allow role to stop builds of the same project
    // prettier-ignore
    const BUILD_CTRL_ARN =
      `arn:aws:codebuild:${awsRegion}:${accountRef}:project/${CODEBUILD_PROJECT_NAME}`;
    PRBuildStack.attachBuildControlPolicy(this, buildRole, serviceNameTag, BUILD_CTRL_ARN);

    // permission to send email
    const SEND_EMAIL_ARN = `arn:aws:ses:${awsRegion}:${accountRef}:identity/*@echobox.com`;
    PRBuildStack.attachSendEmailPolicy(this, buildRole, serviceNameTag, SEND_EMAIL_ARN);

    // If repo does not create containers then some of the permissions
    // are not required

    if (isPrivileged == true) {
      const ECR_REPO_ARN = `arn:aws:ecr:${awsRegion}:${accountRef}:repository/${owner}/${repo}/*`;
      PRBuildStack.attachECRAuthTokenPolicy(this, buildRole, serviceNameTag, ECR_REPO_ARN);

      const EKS_CLUSTER_ARN = `arn:aws:ses:${awsRegion}:${accountRef}:cluster/EchoboxEKS*`;

      PRBuildStack.attachEKSDescribePolicy(this, buildRole, serviceNameTag, EKS_CLUSTER_ARN);

      PRBuildStack.attachHelmS3Policy(this, buildRole, serviceNameTag, helmChartsS3Bucket);
    }

    const BUILDSCRIPT_ARCHIVE_ARN =
      `arn:aws:ssm:${awsRegion}:${accountRef}` +
      `:parameter/Infrastructure/Codebuild/DefaultBuildScriptsLocation`;

    PRBuildStack.attachBuildscriptsS3Policy(
      this,
      buildRole,
      serviceNameTag,
      buildscriptsS3Bucket,
      BUILDSCRIPT_ARCHIVE_ARN
    );

    const CLOUDFORMATION_ARN = `arn:aws:cloudformation:${awsRegion}:${accountRef}:stack/*`;
    PRBuildStack.attachCloudFormationDescribeStacksPolicy(
      this,
      buildRole,
      serviceNameTag,
      CLOUDFORMATION_ARN
    );

    // prettier-ignore
    const BUILD_EMAIL_ARN =
      `arn:aws:ssm:${awsRegion}:${accountRef}:parameter/Prod/Internal/Build-Email`;
    PRBuildStack.attachBuildEmailParamAccessPolicy(
      this,
      buildRole,
      serviceNameTag,
      BUILD_EMAIL_ARN
    );

    // indicate files to cache - this will pick up the paths from the buildspec-pr.yml file
    // note:  this was a change available from 0.32.0 up.
    // No longer require a s3 bucket to save cached data.
    const cache = codebuild.Cache.local(codebuild.LocalCacheMode.CUSTOM);

    // Create build
    new codebuild.Project(this, CODEBUILD_PROJECT_NAME, {
      // render url for build badge
      badge: true,
      cache,
      description: `An AWS codebuild project for repo: ${repo}, created: ${Date()}`,
      // note will use buildspec-pr.yml
      // set the environment
      environment: {
        computeType: computeType,
        buildImage: PRBuildStack.CODEBUILD_IMAGE,
        privileged: isPrivileged
      },
      buildSpec: codebuild.BuildSpec.fromSourceFilename("buildspec-pr.yml"),
      projectName: CODEBUILD_PROJECT_NAME,
      role: buildRole,
      source,
      // The number of minutes after which AWS CodeBuild stops the build if it's not complete
      timeout: Duration.minutes(codebuildTimeoutMins),
      vpc,
      securityGroups: [securityGroup]
    });

    // create Log group
    // Note:  The CodeBuild project will log to the group name derived from the project name
    // therefore the LogGroup MUST be named in the same way otherwise it will log elsewhere
    // configured to delete if the stack is deleted
    new LogGroup(this, serviceNameTag + "-LogGroup", {
      logGroupName: "/aws/codebuild/" + serviceNameTag + "-CodeBuild",
      retention: RetentionDays.ONE_MONTH,
      removalPolicy: RemovalPolicy.DESTROY
    });

    // finally do the billing tags
    ServiceBillingTags.setEnvironment(this, PRBuildStack.ENVIRONMENT_TAG);
    ServiceBillingTags.setServiceType(this, PRBuildStack.SERVICE_TYPE_TAG);
    ServiceBillingTags.setServiceName(this, serviceName);
  }

  private static attachBuildControlPolicy(
    construct: Construct,
    buildRole: Role,
    serviceNameTag: string,
    buildControlArn: string
  ): void {
    // inline policy to allow role to stop builds of the same codebuild project
    const policyStatement = new PolicyStatement();
    const policy = new Policy(construct, `${serviceNameTag}-BuildControl`, {});
    policyStatement.addActions("codebuild:StopBuild");
    policyStatement.addActions("codebuild:StartBuild");
    policyStatement.addActions("codebuild:BatchGetBuilds");
    policyStatement.addActions("codebuild:ListBuildsForProject");
    policyStatement.addResources(buildControlArn);
    policy.addStatements(policyStatement);
    buildRole.attachInlinePolicy(policy);
  }

  private static attachSecretManagerAccessPolicy(
    construct: Construct,
    buildRole: Role,
    serviceNameTag: string,
    secretAccessTokenArn: string
  ): void {
    // build inline policy to allow access to stored parameter holding git token
    const policyStatement = new PolicyStatement();
    const policy = new Policy(construct, `${serviceNameTag}-Secret-SecretManager-Access`, {});
    policyStatement.addActions("secretsmanager:GetSecretValue");
    policyStatement.addResources(secretAccessTokenArn);
    policy.addStatements(policyStatement);
    buildRole.attachInlinePolicy(policy);
  }

  private static attachEKSDescribePolicy(
    construct: Construct,
    buildRole: Role,
    serviceNameTag: string,
    eksClusterArn: string
  ): void {
    // build inline policy to allow EKS describe
    const policyStatement = new PolicyStatement();
    const policy = new Policy(construct, `${serviceNameTag}-EKSDescribe-Access`, {});
    policyStatement.addActions("eks:DescribeCluster");
    policyStatement.addResources(eksClusterArn);
    policy.addStatements(policyStatement);
    buildRole.attachInlinePolicy(policy);
  }

  private static attachHelmS3Policy(
    construct: Construct,
    buildRole: Role,
    serviceNameTag: string,
    helmChartsS3Bucket: string
  ): void {
    // build inline policy for Helm s3 repo access
    const policyStatement = new PolicyStatement();
    const policyStatement2 = new PolicyStatement();
    const policy = new Policy(construct, `${serviceNameTag}-HelmS3-Access`, {});
    policyStatement.addActions("s3:PutObjectAcl");
    policyStatement.addActions("s3:PutObject");
    policyStatement.addActions("s3:GetObjectAcl");
    policyStatement.addActions("s3:GetObject");
    policyStatement.addActions("s3:DeleteObject");
    policyStatement.addResources("arn:aws:s3:::" + helmChartsS3Bucket + "/charts/*");
    policyStatement.addResources("arn:aws:s3:::" + helmChartsS3Bucket + "/charts");
    policy.addStatements(policyStatement);
    policyStatement2.addActions("s3:ListBucket");
    policyStatement2.addResources("arn:aws:s3:::" + helmChartsS3Bucket);
    policy.addStatements(policyStatement2);
    buildRole.attachInlinePolicy(policy);
  }

  private static attachBuildscriptsS3Policy(
    construct: Construct,
    buildRole: Role,
    serviceNameTag: string,
    buildscriptsS3Bucket: string,
    buildScriptsArchiveArn: string
  ): void {
    // build inline policy for Helm s3 repo access
    const policyStatement = new PolicyStatement();
    const policyStatement2 = new PolicyStatement();
    const policyStatement3 = new PolicyStatement();
    const policy = new Policy(construct, `${serviceNameTag}-BuildscriptsS3-Access`, {});
    policyStatement.addActions("s3:PutObjectAcl");
    policyStatement.addActions("s3:PutObject");
    policyStatement.addActions("s3:GetObjectAcl");
    policyStatement.addActions("s3:GetObject");
    policyStatement.addActions("s3:DeleteObject");
    policyStatement.addResources("arn:aws:s3:::" + buildscriptsS3Bucket + "/*");
    policyStatement.addResources("arn:aws:s3:::" + buildscriptsS3Bucket);
    policy.addStatements(policyStatement);
    policyStatement2.addActions("s3:ListBucket");
    policyStatement2.addResources("arn:aws:s3:::" + buildscriptsS3Bucket);
    policy.addStatements(policyStatement2);
    policyStatement3.addActions("ssm:GetParameters");
    policyStatement3.addActions("ssm:GetParameter");
    policyStatement3.addResources(buildScriptsArchiveArn);
    policy.addStatements(policyStatement3);
    buildRole.attachInlinePolicy(policy);
  }

  private static attachECRAuthTokenPolicy(
    construct: Construct,
    buildRole: Role,
    serviceNameTag: string,
    ecrRepoArn: string
  ): void {
    // build inline policy to allow access to stored parameter holding git token
    const policyStatement = new PolicyStatement();
    const policyStatement2 = new PolicyStatement();
    const policy = new Policy(construct, `${serviceNameTag}-ECRAuthToken-Access`, {});
    policyStatement.addActions("ecr:GetAuthorizationToken");
    policyStatement.addActions("ecr:DescribeRepositories");
    policyStatement.addResources("*");
    policy.addStatements(policyStatement);
    policyStatement2.addActions("ecr:PutLifecyclePolicy");
    policyStatement2.addActions("ecr:CreateRepository");
    policyStatement2.addActions("ecr:SetRepositoryPolicy");
    policyStatement2.addActions("ecr:BatchGetImage");
    policyStatement2.addActions("ecr:PutImage");
    policyStatement2.addActions("ecr:InitiateLayerUpload");
    policyStatement2.addActions("ecr:TagResource");
    policyStatement2.addActions("ecr:ListImages");
    policyStatement2.addResources(ecrRepoArn);
    policy.addStatements(policyStatement2);
    buildRole.attachInlinePolicy(policy);
  }

  private static attachSendEmailPolicy(
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

  private static attachCloudFormationDescribeStacksPolicy(
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
}

export default PRBuildStack;
