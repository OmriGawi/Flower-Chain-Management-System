package communication;

import java.io.Serializable;

/**
 * The message that is used to send Data and Tasks and Answers from and to the client.
 */
public class Message implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Task task;
    private Answer answer;
    private Object object;
    private boolean isSendToAll;
    private int senderUserId;

    public Message(Task task, Answer answer, Object object) {
        this.task = task;
        this.answer = answer;
        this.object = object;
        this.isSendToAll = false;
    }

    public Message(Task task, Answer answer) {
        this.task = task;
        this.answer = answer;
        this.object = null;
        this.isSendToAll = false;
    }

    public Message(Task task) {
        this.task = task;
        this.answer = Answer.WAIT_RESPONSE;
        this.object = null;
        this.isSendToAll = false;
    }

    public Message(Task task, Object object) {
        this.task = task;
        this.answer = Answer.WAIT_RESPONSE;
        this.object = object;
        this.isSendToAll = false;
    }

    public Task getTask() {
        return this.task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Answer getAnswer() {
        return this.answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public Object getObject() {
        return this.object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public boolean isSendToAll() {
        return isSendToAll;
    }

    public void setSendToAll(boolean sendToAll) {
        isSendToAll = sendToAll;
    }

    public int getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(int senderUserId) {
        this.senderUserId = senderUserId;
    }
}
