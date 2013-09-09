package com.petrpopov.cheatfood.model.entity;

import com.petrpopov.cheatfood.config.DateSerializer;
import com.petrpopov.cheatfood.config.TimeSerializer;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * User: petrpopov
 * Date: 06.09.13
 * Time: 20:07
 */

@Document
public class Comment implements Comparable<Comment> {

    @Id
    private String id;

    private Date date;

    @Transient
    private Date time;

    @NotNull
    @NotEmpty
    private String text;

    @DBRef
    private UserEntity author;

    private String authorPublicName;

    private List<CommentVote> votes;

    private long votesUpCount;

    private long votesDownCount;

    private String questionCommentId;

    public Comment() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonSerialize(using = DateSerializer.class)
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @JsonSerialize(using = TimeSerializer.class)
    public Date getTime() {
        return date;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public UserEntity getAuthor() {
        return author;
    }

    public void setAuthor(UserEntity author) {
        this.author = author;
    }

    public String getAuthorPublicName() {
        return authorPublicName;
    }

    public void setAuthorPublicName(String authorPublicName) {
        this.authorPublicName = authorPublicName;
    }

    public List<CommentVote> getVotes() {
        return votes;
    }

    public void setVotes(List<CommentVote> votes) {
        this.votes = votes;
    }

    public long getVotesUpCount() {
        return votesUpCount;
    }

    public void setVotesUpCount(long votesUpCount) {
        this.votesUpCount = votesUpCount;
    }

    public long getVotesDownCount() {
        return votesDownCount;
    }

    public void setVotesDownCount(long votesDownCount) {
        this.votesDownCount = votesDownCount;
    }

    public String getQuestionCommentId() {
        return questionCommentId;
    }

    public void setQuestionCommentId(String questionCommentId) {
        this.questionCommentId = questionCommentId;
    }

    @Override
    public int compareTo(Comment other) {

        if( other == null )
            return 1;

        Date thisDate = this.getDate();
        Date otherDate = other.getDate();

        if( thisDate == null && otherDate == null )
            return 0;

        if( thisDate != null && otherDate == null )
            return 1;

        if( otherDate != null && thisDate == null )
            return -1;

        DateTime d = new DateTime(thisDate);
        DateTime n = new DateTime(otherDate);

        return d.compareTo(n);
    }
}
