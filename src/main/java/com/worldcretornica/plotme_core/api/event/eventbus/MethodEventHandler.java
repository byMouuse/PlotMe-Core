/*
 * This file contains code which has been inspired by or modified from
 * the following projects:
 *
 * Sponge, licensed under the MIT License (MIT).
 * Copyright (c) SpongePowered.org <http://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * WorldEdit, licensed under under the terms of the GNU Lesser General Public License version 3.
 * Copyright (C) sk89q <http://www.sk89q.com>
 * Copyright (C) WorldEdit team and contributors
 *
 * Contributors of this repository do not claim ownership of this code and all rights belong to the works
 * this is derived from.
 */

package com.worldcretornica.plotme_core.api.event.eventbus;

import com.worldcretornica.plotme_core.api.event.Event;
import com.worldcretornica.plotme_core.api.event.ICancellable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodEventHandler implements Comparable<MethodEventHandler> {

    private final Object object;
    private final Method method;
    private final Order priority;

    /**
     * Create a new event handler.
     *
     * @param priority the priority of the event
     * @param method the method with the subscribe annotation
     */
    public MethodEventHandler(Order priority, Object object, Method method) {
        this.priority = priority;
        this.object = object;
        this.method = method;
    }

    /**
     * Dispatch the event.
     *
     * @param event the event object
     * @throws Exception an exception that may be thrown
     */
    public void dispatch(Event event) throws Exception {
        if ((event instanceof ICancellable) && ((ICancellable) event).isCancelled()) {
            return;
        }
        method.invoke(object, event);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MethodEventHandler that = (MethodEventHandler) o;

        return method.equals(that.method) && !(object != null ? !object.equals(that.object) : that.object != null);

    }

    @Override
    public int hashCode() {
        int result = object != null ? object.hashCode() : 0;
        result = 31 * result + method.hashCode();
        return result;
    }

    /**
     * Get the priority.
     *
     * @return the priority
     */
    public Order getPriority() {
        return priority;
    }

    /**
     * Dispatch the given event.
     *
     * <p>Subclasses should override {@link #dispatch(Event)}.</p>
     *
     * @param event the event
     * @throws InvocationTargetException thrown if an exception is thrown during dispatch
     */
    public final void handleEvent(Event event) throws InvocationTargetException {
        try {
            dispatch(event);
        } catch (Throwable t) {
            throw new InvocationTargetException(t);
        }
    }

    @Override
    public int compareTo(MethodEventHandler o) {
        return getPriority().ordinal() - o.getPriority().ordinal();
    }

    @Override
    public String toString() {
        return "EventHandler{" +
                "priority=" + priority +
                '}';
    }

}
