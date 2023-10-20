#!groovy
def BN = (BRANCH_NAME == 'master' || BRANCH_NAME.startsWith('releases/')) ? BRANCH_NAME : 'releases/2023-12'

library "knime-pipeline@$BN"

static final String DEFAULT_WF_TESTS_PYTHON_ENV = 'env_py39_kn47.yml'

properties([
    pipelineTriggers([
        upstream("knime-python/${BRANCH_NAME.replaceAll('/', '%2F')}")
    ]),
    parameters(workflowTests.getConfigurationsAsParameters()),
    buildDiscarder(logRotator(numToKeepStr: '5')),
    disableConcurrentBuilds()
])

try {
    // provide the name of the update site project
    knimetools.defaultTychoBuild('org.knime.update.geospatial', 'maven && workflow-tests && java17')

    String envYml = "${DEFAULT_WF_TESTS_PYTHON_ENV}"

    withEnv([ "KNIME_WORKFLOWTEST_PYTHON_ENVIRONMENT=${envYml}" ]) {
        stage("Workflowtests with Python ${envYml}") {
            workflowTests.runTests(
                dependencies: [
                    repositories: [
                        'knime-conda',
                        'knime-core-columnar',
                        'knime-credentials-base',
                        'knime-database',
                        'knime-filehandling',
                        'knime-gateway',
                        'knime-geospatial',
                        'knime-kerberos',
                        'knime-office365',
                        'knime-python',
                        'knime-scripting-editor',
                        'knime-python-legacy',
                        'knime-scripting-editor',
                    ],
                    ius: [
                        'org.knime.features.core.columnar.feature.group',
                        'org.knime.features.geospatial.db.feature.group',
                        'org.knime.features.geospatial.feature.group'
                    ]
                ]
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

/* vim: set shiftwidth=4 expandtab smarttab: */
