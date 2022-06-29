#!groovy
def BN = (BRANCH_NAME == 'master' || BRANCH_NAME.startsWith('releases/')) ? BRANCH_NAME : 'releases/2022-09'

@groovy.transform.Field
static final String[] PYTHON_VERSIONS = ['39']

@groovy.transform.Field
static final String DEFAULT_PYTHON_VERSION = '39'

library "knime-pipeline@$BN"

properties([
    parameters(workflowTests.getConfigurationsAsParameters() + getPythonParameters()),
    buildDiscarder(logRotator(numToKeepStr: '5')),
    disableConcurrentBuilds()
])

try {
    // provide the name of the update site project
    knimetools.defaultTychoBuild('org.knime.update.geospatial', 'maven && python3 && java11')

    withEnv([ "KNIME_WORKFLOWTEST_PYTHON_VERSION=39" ]) {
        stage("Workflowtests") {
            workflowTests.runTests(
                dependencies: [
                    // A list of repositories required for running workflow tests. All repositories that are required for a minimal
                    // KNIME AP installation are added by default and don't need to be specified here. Currently these are:
                    //
                    // 'knime-tp', 'knime-shared', 'knime-core', 'knime-base', 'knime-workbench', 'knime-expressions',
                    // 'knime-js-core','knime-svg', 'knime-product'
                    //
                    // All features (not plug-ins!) in the specified repositories will be installed.
                    repositories: [
                        'knime-geospatial', 
                        'knime-python',
                        'knime-python-legacy',
                        'knime-conda',
                        'knime-filehandling',
                        'knime-core-columnar',
                        'knime-core-arrow'
                    ]
                ],
                extraNodeLabel: 'python-all'
            )
        }
    }

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

/**
* Return parameters to select python version to run workflowtests with
*/
def getPythonParameters() {
    def pythonParams = []
    for (c in PYTHON_VERSIONS) {
        pythonParams += booleanParam(defaultValue: c == DEFAULT_PYTHON_VERSION, description: "Run workflowtests with Python ${c}", name: c)
    }
    return pythonParams
}

/* vim: set shiftwidth=4 expandtab smarttab: */
