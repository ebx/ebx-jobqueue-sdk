#!/usr/bin/env node
import "source-map-support/register";
import cdk = require("@aws-cdk/core");
import { ReleasePipelineNestedStack } from "../Release-Pipelines/lib/codepipeline-stack";
import PRBuildNestedStack from "../PRBuilds/lib/codebuild-stack";

const app = new cdk.App();

const OWNER = app.node.tryGetContext("owner");
const REPO = app.node.tryGetContext("repo");
const GIT_TOKEN_SECRET_ARN = app.node.tryGetContext("git_token_secret_arn");
const GIT_CONNECTION_ARN = app.node.tryGetContext("git_connection_arn");
const SERVICE_NAME = app.node.tryGetContext("service_name");
const ACCOUNT_REF = app.node.tryGetContext("account_ref");
const CODEBUILD_VPC = app.node.tryGetContext("codebuild_vpc");
const CODEBUILD_AZ = app.node.tryGetContext("codebuild_az");
const CODEBUILD_PRIVATE_SUBNET = app.node.tryGetContext("codebuild_private_subnet");
const CODEBUILD_TIMEOUT_MINS = app.node.tryGetContext("codebuild_timeout_mins");
const CREATE_WEBHOOK = app.node.tryGetContext("create_webhook");
const RELEASE_PIPELINE_BRANCH_NAMES = app.node.tryGetContext("release_pipeline_branch_names");
const BUILD_INSTANCE_SIZE = app.node.tryGetContext("build_instance_size");
const AWS_REGION = app.node.tryGetContext("aws_region");
const BUILD_CONTAINER = app.node.tryGetContext("build_container");
const NOTIFICATION_EMAIL = app.node.tryGetContext("notification_email");
const BUILDSCRIPTS_S3_BUCKET = app.node.tryGetContext("buildscripts_s3_bucket");
const HELM_CHARTS_S3_BUCKET = app.node.tryGetContext("helm_charts_s3_bucket");

// create a properties object
const propertiesObject = {
  owner: OWNER,
  repo: REPO,
  git_token_secret_arn: GIT_TOKEN_SECRET_ARN,
  git_connection_arn: GIT_CONNECTION_ARN,
  service_name: SERVICE_NAME,
  account_ref: ACCOUNT_REF,
  codebuild_vpc: CODEBUILD_VPC,
  codebuild_az: CODEBUILD_AZ,
  codebuild_private_subnet: CODEBUILD_PRIVATE_SUBNET,
  codebuild_timeout_mins: CODEBUILD_TIMEOUT_MINS,
  create_webhook: CREATE_WEBHOOK,
  release_pipeline_branch_names: RELEASE_PIPELINE_BRANCH_NAMES,
  build_instance_size: BUILD_INSTANCE_SIZE,
  aws_region: AWS_REGION,
  build_container: BUILD_CONTAINER,
  notification_email: NOTIFICATION_EMAIL,
  buildscripts_s3_bucket: BUILDSCRIPTS_S3_BUCKET,
  helm_charts_s3_bucket: HELM_CHARTS_S3_BUCKET
};

// set the prefix from the cdk.json values
const stackNamePrefix = SERVICE_NAME + "-Service";
const parentStack = new cdk.Stack(app, stackNamePrefix + "-Parent");

// build the pipelines - referencing branches from branch_names_csv
new ReleasePipelineNestedStack(
  parentStack,
  stackNamePrefix + "-Release-Pipelines",
  propertiesObject
);

// build the pr codebuild
new PRBuildNestedStack(parentStack, stackNamePrefix + "-PRBuilds", propertiesObject);
