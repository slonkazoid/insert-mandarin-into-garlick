package pm.n2.tangerine.modules.misc

import gay.eviee.imguiquilt.ImGuiQuilt
import gay.eviee.imguiquilt.interfaces.Renderable
import imgui.ImGui
import imgui.ImVec2
import imgui.flag.ImGuiWindowFlags
import net.minecraft.client.MinecraftClient
import pm.n2.tangerine.Tangerine
import pm.n2.tangerine.gui.ImGuiManager
import pm.n2.tangerine.modules.Module
import pm.n2.tangerine.modules.ModuleCategory

class ModuleListModule : Module("module_list", "Module list", "Shows a list of all enabled modules", ModuleCategory.MISC) {
    private var shouldDraw = false

    private val renderable = object : Renderable {
        override fun getName() = "Tangerine Module List"
        override fun getTheme() = ImGuiManager.theme
        override fun render() {
            if (!shouldDraw) return
            if (!Tangerine.moduleManager.get(ModuleListModule::class).enabled.booleanValue) return

            val windowFlags = ImGuiWindowFlags.NoDecoration or
                    ImGuiWindowFlags.NoInputs or
                    ImGuiWindowFlags.NoBackground or
                    ImGuiWindowFlags.NoBringToFrontOnFocus or
                    ImGuiWindowFlags.NoFocusOnAppearing;

            val moduleListString = StringBuilder()
            var anyModulesEnabled = false

            for (module in Tangerine.moduleManager.modules) {
                if (module.enabled.booleanValue) {
                    anyModulesEnabled = true
                    moduleListString.append(module.name).append("\n")
                }
            }

            if (!anyModulesEnabled) return

            val screenSize = ImGui.getMainViewport().size
            val screenPos = ImGui.getMainViewport().pos
            var size = ImVec2()

            ImGui.calcTextSize(size, moduleListString.toString())
            size = ImVec2(screenSize.x.coerceAtMost(size.x + 25), screenSize.y.coerceAtMost(size.y + 25))

            ImGui.setNextWindowSize(size.x, size.y)
            ImGui.setNextWindowPos(screenPos.x + (screenSize.x - size.x), screenPos.y)
            if (ImGui.begin("##Tangerine Module List", windowFlags)) {
                ImGui.textUnformatted(moduleListString.toString())
            }

            ImGui.end()

        }
    }

    override fun onEndTick(mc: MinecraftClient) {
        val screen = mc.currentScreen
        val lastShouldDraw = shouldDraw

        shouldDraw = screen == null

        if (shouldDraw != lastShouldDraw) {
            if (shouldDraw) {
                ImGuiQuilt.pushRenderable(renderable)
            } else {
                ImGuiQuilt.pullRenderable(renderable)
            }
        }
    }
}