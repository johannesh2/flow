package com.vaadin.hummingbird.kernel.change;

import com.vaadin.hummingbird.kernel.StateNode;

public class ListReplaceChange extends NodeListChange {

    private Object oldValue;

    public ListReplaceChange(int index, Object key, Object oldValue,
            Object newValue) {
        super(index, key, newValue);
        this.oldValue = oldValue;
    }

    /**
     * Returns the previous value of the changed object in the list
     *
     * @return the value of the changed object in the list before the change
     */
    public Object getOldValue() {
        return oldValue;
    }

    @Override
    public void accept(StateNode node, NodeChangeVisitor visitor) {
        visitor.visitListReplaceChange(node, this);
    }

    @Override
    public String toString() {
        return "ListReplaceChange [key=" + getKey() + ", index=" + getIndex()
                + ", value=" + getValue() + ", oldValue=" + oldValue + "]";
    }

}