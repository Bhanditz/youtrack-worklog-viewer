package de.pbauerochse.worklogviewer.fx.components.tabs

import de.pbauerochse.worklogviewer.fx.components.statistics.data.TaskCountByProjectAndUserStatisticData
import de.pbauerochse.worklogviewer.fx.components.statistics.data.TaskCountByUserAndProjectStatisticData
import de.pbauerochse.worklogviewer.fx.components.statistics.panels.TaskCountByUserAndProjectStatistics
import de.pbauerochse.worklogviewer.fx.components.statistics.panels.TimePerProjectAndUserGraphStatistics
import de.pbauerochse.worklogviewer.fx.components.statistics.panels.TimePerUserAndProjectGraphStatistics
import de.pbauerochse.worklogviewer.report.Issue
import de.pbauerochse.worklogviewer.report.TimeReport
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import javafx.scene.Node
import org.slf4j.LoggerFactory

/**
 * Tab, that shows all worklogs contained in the
 * [TimeReport], no matter what author or project
 */
internal class AllWorklogsTab : WorklogsTab(getFormatted("view.main.tabs.all")) {

    fun update(report: TimeReport) {
        LOGGER.debug("Showing all worklogs")
        val allIssues = report.issues
//            .filter { it.hasTicketsWithOwnWorklogs() } ???
//            .flatMap { it.issues }
            .sorted()

        update(text, report.reportParameters, allIssues)
    }

    override fun getStatistics(issues: List<Issue>): List<Node> {
        val dataByUser = TaskCountByUserAndProjectStatisticData(issues)
        val dataByProject = TaskCountByProjectAndUserStatisticData(issues)
        return arrayListOf(
            TaskCountByUserAndProjectStatistics(dataByUser),
            TimePerProjectAndUserGraphStatistics(dataByUser),
            TimePerUserAndProjectGraphStatistics(dataByProject)
        )
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(AllWorklogsTab::class.java)
    }
}
