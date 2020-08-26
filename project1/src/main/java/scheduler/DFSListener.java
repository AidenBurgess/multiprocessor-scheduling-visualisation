package main.java.scheduler;

public interface DFSListener {
    /**
     * Hook method that is called on every run() entry
     * Responsible for updating information in the InformationHolder, if necessary
     */
    void onDFSEntry();

    /**
     * Hook method that is called on every run() exit
     * Responsible for updating information in the InformationHolder, if necessary
     */
    void onDFSExit();

    /**
     * Hook method that is called on every new complete schedule that is found
     * Responsible for updating information in the InformationHolder, if necessary
     */
    void onCompleteSchedule(State state, Bound bound);

    void onPartialSchedule(State state, Bound bound);
}
