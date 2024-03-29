# [Service-Name]
Latest Release: vX.X.X (Using SemVer).

Delete as required:
![Service has been extracted from the monolith and can be independantly deployed.](https://img.shields.io/badge/Is%20Independent%3F-True-brightgreen)
![Service is still part of the monolith](https://img.shields.io/badge/Is%20Independent%3F-False-red)
[![SLA-Gold](https://img.shields.io/badge/SLA-Gold-yellow)](https://ebx.sh/sla-gold)
[![SLA-Silver](https://img.shields.io/badge/SLA-Silver-lightgrey)](https://ebx.sh/sla-silver)
[![SLA-Beta](https://img.shields.io/badge/SLA-Beta-red)](https://ebx.sh/sla-beta)

General documentation reminders:
* Large documents are never kept up to date, plus no-one reads them, so be concise. Opt for less
rather than more.
* Automate documentation relentlessly. 

A happy microservice ensures:
* Async communication preferred for back office operations.
* All out of service calls use circuit breakers.
* As much as possible cross cutting concerns such as auth, logging, circuit breaks go into the 
service mesh.

## Purpose
Identify the service’s purpose, role and business use case (how this is related to the wider 
product) in a paragraph.

## Supported Languages
For anything AI related. If this isn't relevant please write 'Not Applicable', rather than removing.

## FAQ
Commonly asked **technical** questions about this service. Product FAQs should go in the product 
documentation.

## Observability
It's very important that all services are observable. It should be possible at a quick glance
to understand the overall health and relative performance of a service, ideally with access to 
historical information. Consider perhaps loggly searches, semantic monitoring, new relic, 
[prometheus](https://prometheus.io/), [grafana](https://grafana.com/), 
[kibana](https://github.com/elastic/kibana), cloudwatch, [x-ray](https://aws.amazon.com/xray/) 
dashboards etc.

Additionally for observability across services:
1. 'X-Correlation-Id' headers should be included in any downstream calls. If the header already 
exists it should be preserved.
2. If a request originated from a user their Id Token should be persisted in the 'Forwarded' 
header. Whilst waiting for this JWT to become available use: `Forwarded: 'UserId:1234'`

Please include the health check endpoint if this is an independent service.

## Runbooks
Detail how to handle each possible alert the service can generate.

## Deployment Guide 
What steps should be taken to deploy changes to this service. Ideally raise PR, merge into 
development branch to deploy to stage and finally merge to master should deploy to production.

## Documentation Endpoints 
### API
Endpoint where the swagger/OpenAPI data can be accessed that documents the service interface. 
This should be autogenerated by the service. If this isn’t available yet please leave as 
‘Not yet available’.

This should include information on how is authentication handled by this service? how is 
authorisation handled by this service? What rate limits are in place?

### Source
If relevant any further online autogenerated documentation, such as javadoc. If not available
please leave as 'Not yet available'.

## Architecture 
Complexity of the microservice ideally as a high-level diagram so the flow can be followed both 
internal and external if required. If the architecture can be explained in text form, as it’s 
simple, no need for a diagram. If a diagram is required, create a new subfolder
called "images" and reference the image as follows in your Markdown:  

`````![caption](./images/good_code.png)`````  
![How to write good code](./images/good_code.png)  

We must keep a record of "architecturally significant" decisions: those that affect the structure, 
non-functional characteristics, dependencies, interfaces, or construction techniques. Until we 
discover a better solution such decisions should be recorded following the 
[ADR](http://thinkrelevance.com/blog/2011/11/15/documenting-architecture-decisions) convention,
putting the records in "/Documentation/adr/".
If such decisions were made in the technical planning phase of work they should be persisted in
source control as ADRs during implementation. [adr-tools](https://github.com/npryce/adr-tools) can 
help manage these records.

## Onboarding Guide 
Get new developers introducing changes without hand-holding. This section should also try to
replace as much of the manual face-to-face onboarding devlopers currently have to go through 
when joining Echobox.

How do you get the code and build it locally. Ideally no manual setup should be required - all 
service dependencies should be defined as docker containers, so only single command like 
"docker-compose up" should be enough to launch all dependencies locally.

Example code that can be run see this service in action. Ideally all examples should be a single 
command line execution (assuming developer has necessary dependencies installed).

## Relevant Drive Documentation 
Particularly important for anything data science related. Link to all potential R&D documents 
or investigations related to this service. If there are no drive documents please leave as 
'None'.

## Dependencies 
Would ideally be automated ;) What other services does this service depend on?

**Invokes:**
Services the current service invokes synchronously or other projects that are referenced. If 
this service is not yet independent then ALL interactions with other parts of the application
will be 'invokes'.

**Subscribes to:**
Services the current services subscribes/listens to. This is reserved for future async 
dependencies, so if there arn't any please leave as 'None'.

## Release Notes 
Should be automated. For now the simple solution is to ensure a suitable JIRA component exists for 
the service and then replace [ServiceComponent] in the following link - 
[JIRA](https://echobox.atlassian.net/issues/?jql=status%20%3D%20Resolved%20AND%20component%20%3D%20[ServiceComponent]%20ORDER%20BY%20updated%20DESC)

In future we can potentially do something more sophisticated, as there are many JIRA 'release notes'
plugins.
