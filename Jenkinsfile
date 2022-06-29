#!groovy
def BN = (BRANCH_NAME == 'master' || BRANCH_NAME.startsWith('releases/')) ? BRANCH_NAME : 'releases/2022-09'

library "knime-pipeline@$BN"

properties([
    buildDiscarder(logRotator(numToKeepStr: '5')),
    disableConcurrentBuilds()
])

try {
    // provide the name of the update site project
    knimetools.defaultTychoBuild('org.knime.update.geospatial')

    // Specifying configurations is optional. If omitted, the default configurations will be used
    // (see jenkins-pipeline-libraries/vars/workflowTests.groovy).
    // In almost all cases you can *remove* this defintion.
    def testConfigurations = [
        "ubuntu20.04"
    ]

    workflowTests.runTests(
        dependencies: [
            // A list of repositories required for running workflow tests. All repositories that are required for a minimal
            // KNIME AP installation are added by default and don't need to be specified here. Currently these are:
            //
            // 'knime-tp', 'knime-shared', 'knime-core', 'knime-base', 'knime-workbench', 'knime-expressions',
            // 'knime-js-core','knime-svg', 'knime-product'
            //
            // All features (not plug-ins!) in the specified repositories will be installed.
            repositories: ['knime-geospatial', 'knime-python']
        ],
        // this is optional and defaults to false
        withAssertions: true,
        // this is optional and only needs to be provided if non-default configurations are used, see above
        configurations: testConfigurations
    )

    stage('Sonarqube analysis') {
        env.lastStage = env.STAGE_NAME
        // Passing the test configuration is optional but must be done when they are used above in the workflow tests.
        // Therefore you can *remove* the argument in almost all cases.
        // In case you don't have any workflow tests but still want a Sonarqube analysis, pass an empty list, i.e. [].
        workflowTests.runSonar(testConfigurations)
    }
} catch (ex) {
    currentBuild.result = 'FAILURE'
    throw ex
} finally {
    notifications.notifyBuild(currentBuild.result);
}

/* vim: set shiftwidth=4 expandtab smarttab: */
