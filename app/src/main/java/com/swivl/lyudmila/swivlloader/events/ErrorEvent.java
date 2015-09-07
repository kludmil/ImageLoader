package com.swivl.lyudmila.swivlloader.events;

import android.content.Context;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Lyudmila on 06.09.2015.
 */
public class ErrorEvent extends BaseEvent{
    @NotNull public final String error;

    public ErrorEvent(final Context context, final double sessionId, @NotNull final Throwable ex){
    	super(sessionId);
        this.error = ex.getMessage();
    }

    @Override
    public String toString() {
        return "ErrorEvent{" +
                "sessionId=" + getSessionId()+
                " , error='" + error + '\'' +
                '}';
    }
}
