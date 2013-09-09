package com.petrpopov.cheatfood.web.filters;

import com.petrpopov.cheatfood.model.entity.Comment;
import com.petrpopov.cheatfood.model.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * User: petrpopov
 * Date: 09.09.13
 * Time: 13:46
 */

@Component
public class CommentFilter {

    @Autowired
    private UserEntityFilter userEntityFilter;

    @Autowired
    private CommentVoteFilter commentVoteFilter;

    public Comment filterComment(Comment comment) {

        if( comment == null )
            return null;

        UserEntity author = comment.getAuthor();
        if( author == null )
            return comment;

        String publicName = userEntityFilter.getPublicName(author);
        comment.setAuthorPublicName(publicName);

        commentVoteFilter.filterCommentVotes(comment.getVotes());

        return comment;
    }

    public List<Comment> filterComments(List<Comment> list) {

        if( list == null )
            return null;

        for (Comment comment : list) {
            filterComment(comment);
        }

        return list;
    }
}
