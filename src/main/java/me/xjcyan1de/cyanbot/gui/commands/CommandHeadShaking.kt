package me.xjcyan1de.cyanbot.gui.commands

import com.intellij.uiDesigner.core.GridConstraints
import com.intellij.uiDesigner.core.GridLayoutManager
import me.xjcyan1de.cyanbot.Bot
import me.xjcyan1de.cyanbot.Main
import me.xjcyan1de.cyanbot.utils.schedule.Schedule
import java.awt.Dimension
import java.awt.Insets
import java.util.*
import javax.swing.*

class CommandHeadShaking : Command("Вращение") {
    private val enabledMap = HashMap<Bot, Boolean>()
    private val timerTaskMap = HashMap<Bot, TimerTask>()

    override fun initPanel(commandPanel: JPanel) {
        val spinPanel = JPanel()
        spinPanel.layout = GridLayoutManager(3, 1, Insets(0, 0, 0, 0), -1, -1)
        spinPanel.border = BorderFactory.createTitledBorder("Кивание")

        val spinField = JTextField()
        spinField.text = "0"

        val spinSlider = JSlider()
        spinSlider.maximum = 50
        spinSlider.minimum = -50
        spinSlider.value = 0
        spinSlider.addChangeListener { _ -> spinField.text = spinSlider.value.toString() }

        spinField.addActionListener { e ->
            run {
                val actionCommand = e.actionCommand
                var value = 0
                try {
                    value = Integer.parseInt(actionCommand)
                } catch (ignored: NumberFormatException) {
                }
                spinSlider.value = value
            }
        }

        val spinButton = JButton()
        spinButton.text = "Включить/Выключить"
        spinButton.addActionListener { _ ->
            run {
                val selectedBots = Main.getMainFrame().selectedBots
                for (bot in selectedBots) {
                    var enabled = enabledMap.getOrDefault(bot, false)
                    var timerTask: TimerTask? = timerTaskMap.getOrDefault(bot, null)
                    enabled = !enabled
                    if (enabled) {
                        var upOrDown = true
                        timerTask = Schedule.timer({
                            val loc = bot.loc

                            if (upOrDown) loc.pitch += spinSlider.value
                            else loc.pitch -= spinSlider.value

                            if (loc.pitch > 45) {
                                upOrDown = false
                            } else if (loc.pitch < -45) {
                                upOrDown = true
                            }
                        }, 0, 50)
                    } else {
                        timerTask?.cancel()
                    }
                    enabledMap.put(bot, enabled)
                    timerTask?.let { timerTaskMap.put(bot, it) }
                }
            }
        }

        spinPanel.add(spinButton, GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK or GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false))
        spinPanel.add(spinField, GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, Dimension(150, -1), null, 0, false))
        spinPanel.add(spinSlider, GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false))
        commandPanel.add(spinPanel, GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK or GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK or GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false))
        spinPanel.rootPane.defaultButton = spinButton
    }
}
