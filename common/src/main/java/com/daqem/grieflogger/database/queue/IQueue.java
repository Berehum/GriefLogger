package com.daqem.grieflogger.database.queue;

public interface IQueue {

    void add(SqlTask sqlTask);
    void execute();
    void hello();
    boolean isEmpty();
}
