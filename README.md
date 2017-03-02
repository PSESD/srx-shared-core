# SRX Shared Core
***
Core Student Record Exchange functions shared by SRX components and services.

***
## Configuration
***

### Environment variables
Use of this library requires several environment variables to be set.
These must be set on the target host environment for deployment.
Alternately, for local development, create a file named 'env-local.properties' in the project root and add them there.

Following is a description of each variable:

Variable 					            | Description 																									| Example
--------- 					            | ----------- 																									| -------
AES_PASSWORD                            | Password used to decrypt AES encrypted request body payloads.                                                 | (see Heroku)                                                                                                        |
AES_SALT                                | Salt used to decrypt AES encrypted request body payloads.                                                     | (see Heroku)                                                                                                     |
AMAZON_S3_ACCESS_KEY                    | AWS S3 access key for SRX cache.                                                                              | (see Heroku)
AMAZON_S3_BUCKET_NAME                   | AWS S3 bucket name for SRX cache.                                                                             | (see Heroku)
AMAZON_S3_PATH                          | Root path to files within SRX cache.                                                                          | (see Heroku)
AMAZON_S3_SECRET                        | AWS S3 secret for SRX cache.                                                                                  | (see Heroku)
AMAZON_S3_TIMEOUT                       | Timeout for AWS S3 connections.                                                                               | 300000
ENVIRONMENT 				            | Name of deployment environment (i.e. development, test, production). Set to 'local' for local development.	| local
LOG_LEVEL 					            | Level of logging for named environment (debug, info, warning, error, critical).								| debug
ROLLBAR_ACCESS_TOKEN 		            | Access token for Rollbar web service (required by Logger).													| (see PSESD administrator)
ROLLBAR_URL 				            | Url for Rollbar web service (required by Logger).																| https://api.rollbar.com/api/1/item/
SERVER_API_ROOT                         | Root path for this service.                                                                                   | (typically leave blank)
SERVER_HOST                             | Host IP for this service.                                                                                     | 127.0.0.1
SERVER_NAME                             | Server name for this service.                                                                                 | localhost
SERVER_PORT                             | Port this service listens on.                                                                                 | 8080
SERVER_URL                              | URL for this service.                                                                                         | http://localhost
SRX_ENVIRONMENT_URL                     | HostedZone environment URL.                                                                                   | https://example.hostedzone.com/svcs/dev/requestProvider
SRX_SESSION_TOKEN                       | HostedZone session token assigned to this service.                                                            | (see HostedZone configuration)
SRX_SHARED_SECRET                       | HostedZone shared secret assigned to this service.                                                            | (see HostedZone configuration)
SRX_TEST_AMAZON_S3_ACCESS_KEY           | AWS S3 access key for SRX cache for test environment.                                                         | (see Heroku)
SRX_TEST_AMAZON_S3_BUCKET_NAME          | AWS S3 bucket name for SRX cache for test environment.                                                        | (see Heroku)
SRX_TEST_AMAZON_S3_PATH                 | Root path to files within SRX cache for test environment.                                                     | (see Heroku)
SRX_TEST_AMAZON_S3_SECRET               | AWS S3 secret for SRX cache for test environment.                                                             | (see Heroku)
SRX_TEST_AMAZON_S3_TIMEOUT              | Timeout for AWS S3 connections for test environment.                                                          | 300000

***
## Copyright
***

Copyright (c) 2016 Puget Sound Educational Service District

This project is released under the [MIT License](https://github.com/PSESD/srx-shared-core/blob/master/LICENSE.md).

********