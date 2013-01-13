package org.test.cameraMonitor.entities;

import org.hibernate.Query;
import org.test.cameraMonitor.util.HibernateUtil;

import javax.persistence.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dreambrotherirl
 * Date: 13/01/2013
 * Time: 11:29
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table (name = "RECORDED_STREAM")
public class RecordedStream {

    private String queryString = "FROM RecordedImage WHERE Date > :start AND Date < :end ORDER BY Date ASC";

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;

    @Column
    private String name;

    @Column
    private long startTime;

    @Column
    private long endTime;

    @Column
    private long comments;

    @Column
    private boolean persist;

    public RecordedStream() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getComments() {
        return comments;
    }

    public void setComments(long comments) {
        this.comments = comments;
    }

    public boolean isPersist() {
        return persist;
    }

    public void setPersist(boolean persist) {
        this.persist = persist;
    }

    public List<RecordedImage> getStream(){
        Query query = HibernateUtil.getSessionFactory().openSession().createQuery(this.queryString);
        query.setParameter("start", this.getStartTime());
        query.setParameter("end", this.getEndTime());
        List<RecordedImage> list = query.list();
        return query.list();
    }
}
