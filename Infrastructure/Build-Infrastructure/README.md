# Build Pipeline and CodeBuild projects

This wrapper project will deploy codebuild and codepipeline projects

To ensure that `npm install` is executed for each project then run the following in this directory.

```
find ../. ! -path "*/node_modules/*" -name "package.json" -execdir npm install \;
```

Now to run build and deploy:

```
npm run build
cdk synth
cdk deploy
```

All the configuration is maintained in `cdk.json` in this directory.

`create_webhook` indicates if the webhook should be created. When applying in production then the webhook should definitely be created. 

However, elevated github permissions are required so this option allows the pipeline and codebuild projects to be created without failure related to this authority.



```
"branch_names_csv": "dev,master"
```

.. indicates which branches to create pipelines for.

```
"build_instance_size": "SMALL",
```

.. indicates compute type available to the build. This can be `SMALL`, `MEDIUM`, `LARGE` and `XLARGE`. It will default to `SMALL` which is appropriate for most builds.

`XLARGE` should never be required. Remember that there is a cost implication to using each compute type setting.

https://aws.amazon.com/codebuild/pricing/

The wrapper project will create 3 stacks: A parent stack and 2 nested stacks. Deployment of the parent stack will manage the deployment of related nested stacks.

