package main.java.scheduler;

import main.java.dotio.TaskGraph;

public class StatisticsScheduler extends PerformanceScheduler {
    InformationHolder retriever;

    public StatisticsScheduler(TaskGraph taskGraph, int processors) {
        super(taskGraph, processors);

        retriever = new InformationHolder();
    }

    @Override
    protected DFS getDFS(State state) {
        return new StatsDFS(state);
    }

    class StatsDFS extends DFS {
        public StatsDFS(State state) {
            super(state);
        }

        @Override
        protected void onDFSEntry() {
            retriever.incrementActiveBranches();
            retriever.incrementTotalStates();
        }

        @Override
        protected void onDFSExit() {
            retriever.decrementActiveBranches();
        }

        @Override
        protected void onCompleteSchedule() {
            retriever.incrementCompleteSchedule();
        }
    }
}
