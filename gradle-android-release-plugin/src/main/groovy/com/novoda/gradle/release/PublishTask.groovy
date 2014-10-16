package com.novoda.gradle.release;

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class PublishTask extends DefaultTask {

    @TaskAction
    def mainAction() {
        println '----------Publish Successful-------------'
    }
}