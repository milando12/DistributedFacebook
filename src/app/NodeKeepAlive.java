package app;

public class NodeKeepAlive {

    private volatile int status;    // 0 - alive, 1 - suspicious, 2 - dead

    // timestamp of the last ping message (resets on every pong message)
    private volatile long timestamp;


    /*
    *  KeepAlive will send broadcast message after the timestamp is older than the weak limit and set doneBroadcast to true.
    *  If listener gets the message that the node is alive (either by node or others in the system) it will reset the timestamp
    *  and set status to alive, and doneBroadcast to false
    */
    private volatile boolean doneBroadcast;

    public NodeKeepAlive() {
        this.status = 0;
        this.timestamp = System.currentTimeMillis();
        this.doneBroadcast = false;
    }

    public int getStatus() {
        return status;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isDoneBroadcast() {
        return doneBroadcast;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setDoneBroadcast(boolean doneBroadcast) {
        this.doneBroadcast = doneBroadcast;
    }

    public void resetTimestamp() {
        this.timestamp = System.currentTimeMillis();
    }

}
