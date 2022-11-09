package net.warp.plugin.warpskiller.Tasks;

import net.unethicalite.api.plugins.Task;
import net.warp.plugin.warpskiller.WarpSkillerPlugin;

public class FletchTask implements Task {
    public FletchTask(WarpSkillerPlugin warpSkillerPlugin) {
    }

    @Override
    public boolean validate() {
        return false;
    }

    @Override
    public int execute() {
        return 0;
    }
}
