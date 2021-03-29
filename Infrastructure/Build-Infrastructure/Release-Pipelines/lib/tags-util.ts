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

import { Construct, Tag } from "@aws-cdk/core";

export enum Environment {
  Production,
  Staging,
  Review,
  Development
}

export enum ServiceType {
  Compute,
  Database,
  Webservice,
  UI,
  ELB,
  Cache,
  S3,
  Backup,
  CDN,
  Redshift,
  Lambda,
  CI
}

/**
 * Class to centralise adding billing tags to service constructs
 */
export class ServiceBillingTags {
  public static setEnvironment(construct: Construct, environment: Environment): boolean {
    construct.node.applyAspect(new Tag("billing:environment", Environment[environment]));
    return true;
  }

  public static setServiceName(construct: Construct, serviceName: string): boolean {
    construct.node.applyAspect(new Tag("billing:servicename", serviceName));
    return true;
  }

  public static setServiceType(construct: Construct, serviceType: ServiceType): boolean {
    construct.node.applyAspect(new Tag("billing:servicetype", ServiceType[serviceType]));
    return true;
  }
}
