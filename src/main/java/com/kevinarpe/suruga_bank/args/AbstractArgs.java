package com.kevinarpe.suruga_bank.args;

import com.beust.jcommander.Parameter;

/**
 * @author Kevin Connor ARPE (kevinarpe@gmail.com)
 */
public abstract class AbstractArgs
implements Args {

    @Parameter(
        names = {"-h", "--help"},
        required = false,
        help = true,
        description = "Display help"
    )
    private boolean isHelpRequested;

    @Override
    public final boolean isHelpRequested() {
        return isHelpRequested;
    }
}
