# ${git_repo}

## Post Creation Steps

### Check Repo Settings

* Set `dev` as the default branch
* Ensure `master` branch is protected
* Invite collaborators for repo
* Enable dependancy graphs, dependabot alerts and security updates if required

### Install Dependent Apps For Building Pipelines

* eksctl
* cdk

```bash
curl --silent --location "https://github.com/weaveworks/eksctl/releases/latest/download/eksctl_$(uname -s)_amd64.tar.gz" | tar xz -C /tmp

mv /tmp/eksctl /usr/local/bin

sudo npm install -g aws-cdk
```

### Deploying Pipelines and Codebuild projects

The CDK project in `Infrastructure/Build-Infrastructure` will deploy one top level stack with two nesting stacks. One for pipeline projects and another for PR build.

The configuration for all off this stored in `cdk.json`.

The parameter `release_pipeline_branch_names` is a comma delimited list of all the branches to build pipelines for.  eg. `dev,master`.


### Setting up Documentation

Markdown files in the `Documentation` directory are uploaded to readme.io.
To ensure this can happen then you need to set a `category` in the `rdme_project.json` file in the `Documentation` directory.

Here is a sample `rdme_project.json`

```json
{
  "category": "[CATEGORY REFERENCE HERE]"
}
```

To get the category id you can call the API in the following manner:

```bash
curl --request GET \
  --url 'https://dash.readme.io/api/v1/categories?perPage=100&page=1' \
  -u '[API TOKEN HERE]' >categories.json
```

The API token can be accessed in the Stored Parameter `/Prod/External/Readme.io/APIToken`

From the command line ..

```bash
aws ssm get-parameter --name "/Prod/External/Readme.io/APIToken" --with-decryption --query "Parameter.Value" --output text
```

Here is an example of the output from the categories command:

```json
    {
        "title": "Scoring Service",
        "slug": "scoring-service",
        "order": 9999,
        "reference": false,
        "isAPI": false,
        "_id": "5e57b84a4afc3d0018569ffc",
        "project": "5ddfc45378e929003c67504a",
        "version": "5ddfc45378e929003c67504e",
        "createdAt": "2020-02-27T12:38:34.077Z",
        "__v": 0
    },
```

The category id is `_id` for the appropriate category. Add this to the `rdme_project.json` file in the `Documentation` directory

### Build Scripts

Buildscripts for this repo are maintained in repo: `codebuild-buildscripts-sdk`\
When the codebuild project runs it will execute `Build-Scripts/get_latest_buildscripts.sh`\
​This is the **only** build script that should be maintained in this repo\
​This script will download a zip file from s3 then uzip the set of scripts into the `Build-Scripts` directory

### Note

If moving or renaming the `Build-{suffix}` directories, please reflect those changes within the buildspec files. Additionally, if moving or renaming the `Build-Infrastructure` directory, please modify the python script `load_config.py` which is located within the `repobootstrap-sdk` repository
