package de.pbauerochse.worklogviewer.fx.tasks

import de.pbauerochse.worklogviewer.util.FormattingUtil
import javafx.css.Styleable
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import org.slf4j.LoggerFactory
import java.util.concurrent.Future
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Starts async Tasks and sets the UI state
 * according to the Task state
 */
class TaskRunner(
    private val parent: Pane,
    private val waitScreenOverlay: StackPane
) {

    /**
     * Starts a thread performing the given task
     * @param task The task to perform
     */
    @Suppress("UNCHECKED_CAST")
    fun <T> startTask(task: WorklogViewerTask<T>): Future<T> {
        LOGGER.info("Starting task ${task.label}")
        val progressBar = TaskProgressBar(task)
        task.stateProperty().addListener(progressBar)

        bindOnRunning(task, progressBar)
        bindOnSucceeded(task, progressBar)
        bindOnFailed(task, progressBar)

        task.stateProperty().addListener { _, oldValue, newValue -> LOGGER.debug("Task ${task.label} changed from $oldValue to $newValue") }

        parent.children.add(progressBar)
        return EXECUTOR.submit(task) as Future<T>
    }

    /**
     * While the task is running, the waitScreenOverlay
     * will be shown and the progress bar and the text bound
     * to the Task
     */
    private fun bindOnRunning(task: WorklogViewerTask<*>, progressBar: TaskProgressBar) {
        val initialOnRunningHandler = task.onRunning
        task.setOnRunning { event ->
            waitScreenOverlay.isVisible = true
            progressBar.progressText.textProperty().bind(task.messageProperty())
            progressBar.progressBar.progressProperty().bind(task.progressProperty())

            setStyle(RUNNING_CLASS, progressBar.progressBar)
            setStyle(RUNNING_CLASS, progressBar.progressText)

            initialOnRunningHandler?.handle(event)
        }
    }

    private fun bindOnSucceeded(task: WorklogViewerTask<*>, progressBar: TaskProgressBar) {
        val initialOnSucceededHandler = task.onSucceeded
        task.setOnSucceeded { event ->
            LOGGER.info("Task {} succeeded", task.title)

            // unbind progress indicators
            progressBar.progressText.textProperty().unbind()
            progressBar.progressBar.progressProperty().unbind()

            setStyle(SUCCESSFUL_CLASS, progressBar.progressBar)
            setStyle(SUCCESSFUL_CLASS, progressBar.progressText)

            if (initialOnSucceededHandler != null) {
                LOGGER.debug("Delegating Event to previous onSucceeded event handler")
                initialOnSucceededHandler.handle(event)
            }

            waitScreenOverlay.isVisible = false
        }
    }

    private fun bindOnFailed(task: WorklogViewerTask<*>, progressBar: TaskProgressBar) {
        val initialOnFailedHandler = task.onFailed
        task.setOnFailed { event ->
            LOGGER.warn("Task {} failed", task.title)

            // unbind progress indicators
            progressBar.progressText.textProperty().unbind()
            progressBar.progressBar.progressProperty().unbind()

            setStyle(ERROR_CLASS, progressBar.progressBar)
            setStyle(ERROR_CLASS, progressBar.progressText)

            if (initialOnFailedHandler != null) {
                LOGGER.debug("Delegating Event to previous onFailed event handler")
                initialOnFailedHandler.handle(event)
            }

            val throwable = event.source.exception
            if (throwable != null && throwable.message.isNullOrBlank().not()) {
                LOGGER.warn("Showing error to user", throwable)
                progressBar.progressText.text = throwable.message
            } else {
                if (throwable != null) {
                    LOGGER.warn("Error executing task {}", task.toString(), throwable)
                }

                progressBar.progressText.text = FormattingUtil.getFormatted("exceptions.main.worker.unknown")
            }

            progressBar.progressBar.progress = 1.0
            waitScreenOverlay.isVisible = false
        }
    }

    private fun setStyle(style: String, stylable: Styleable) {
        stylable.styleClass.removeAll(ERROR_CLASS, RUNNING_CLASS, SUCCESSFUL_CLASS)
        stylable.styleClass.add(style)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(TaskRunner::class.java)

        private const val ERROR_CLASS = "error"
        private const val RUNNING_CLASS = "running"
        private const val SUCCESSFUL_CLASS = "success"

        @JvmStatic
        val EXECUTOR = ThreadPoolExecutor(1, 1, 1, TimeUnit.MINUTES, LinkedBlockingQueue())
    }

}