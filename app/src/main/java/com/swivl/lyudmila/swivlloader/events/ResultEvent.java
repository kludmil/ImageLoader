package com.swivl.lyudmila.swivlloader.events;

import com.swivl.lyudmila.swivlloader.data.ListData;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Lyudmila on 06.09.2015.
 */
public class ResultEvent extends BaseEvent {
    @NotNull public final ListData[] listData;
    @NotNull public final double sessionId;

    public ResultEvent(double sessionId, @NotNull ListData[] listData){
    	super(sessionId);
        this.listData = listData;
        this.sessionId = sessionId;
    }
}
