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
                    repositories: [
                        'knime-geospatial',
                        'knime-python',
                        'knime-python-legacy',
                        'knime-conda',
                        'knime-filehandling',
                        'knime-core-columnar',
                        'knime-core-arrow'
                    ],
                    ius: [
                        'org.knime.features.core.columnar.feature.group'
                    ]
                ],
                extraNodeLabel: 'python-all'
            )
        }
    }

    stage('Sonarqube analysis') {
        env.lastStage = env.STAGE_NAME
        workflowTests.runSonar()
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
