package com.petrpopov.cheatfood.web.filters;

import com.petrpopov.cheatfood.model.entity.CommentVote;
import com.petrpopov.cheatfood.model.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * User: petrpopov
 * Date: 09.09.13
 * Time: 13:52
 */

@Component
public class CommentVoteFilter {

    @Autowired
    private UserEntityFilter userEntityFilter;

    public CommentVote filterCommentVote(CommentVote vote) {

        if( vote == null )
            return null;

        UserEntity author = vote.getAuthor();
        if( author == null )
            return vote;

        String publicName = userEntityFilter.getPublicName(author);
        vote.setAuthorPublicName(publicName);

        return vote;
    }

    public List<CommentVote> filterCommentVotes(List<CommentVote> list) {
        if( list == null )
            return list;

        for (CommentVote commentVote : list) {
            filterCommentVote(commentVote);
        }

        return list;
    }
}
