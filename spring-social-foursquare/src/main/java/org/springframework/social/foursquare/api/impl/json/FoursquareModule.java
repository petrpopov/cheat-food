package org.springframework.social.foursquare.api.impl.json;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.module.SimpleModule;
import org.springframework.social.foursquare.api.*;

public class FoursquareModule extends SimpleModule {

	public FoursquareModule() {
		super(FoursquareModule.class.getName(), new Version(1, 0, 0, null));
	}
	
	@Override 
	public void setupModule(SetupContext context) {
		context.setMixInAnnotations(AllSettings.class, AllSettingsMixin.class);
		context.setMixInAnnotations(Badge.class, BadgeMixin.class);
	    context.setMixInAnnotations(BadgeGroup.class, BadgeGroupMixin.class);
	    context.setMixInAnnotations(BadgeImage.class, BadgeImageMixin.class);
	    context.setMixInAnnotations(BadgeSets.class, BadgeSetsMixin.class);
	    context.setMixInAnnotations(BadgesResponse.class, BadgesResponseMixin.class);
	    context.setMixInAnnotations(BadgeUnlocks.class, BadgeUnlocksMixin.class);
	    context.setMixInAnnotations(Category.class, CategoryMixin.class);
		context.setMixInAnnotations(Checkin.class, CheckinMixin.class);
		context.setMixInAnnotations(CheckinSource.class, CheckinSourceMixin.class);
		context.setMixInAnnotations(CheckinComment.class, CheckinCommentMixin.class);
		context.setMixInAnnotations(CheckinCommentInfo.class, CheckinCommentInfoMixin.class);
		context.setMixInAnnotations(CheckinInfo.class, CheckinInfoMixin.class);
		context.setMixInAnnotations(ContactInfo.class, ContactInfoMixin.class);
		context.setMixInAnnotations(ExploreResponse.class, ExploreResponseMixin.class);
		context.setMixInAnnotations(FoursquareUser.class, FoursquareUserMixin.class);
		context.setMixInAnnotations(FoursquareUserGroup.class, FoursquareUserGroupMixin.class);
		context.setMixInAnnotations(FriendInfo.class, FriendInfoMixin.class);
		context.setMixInAnnotations(Friends.class, FriendsMixin.class);
		context.setMixInAnnotations(HereNow.class, HereNowMixin.class);
		context.setMixInAnnotations(Keyword.class, KeywordMixin.class);
		context.setMixInAnnotations(Keywords.class, KeywordsMixin.class);
		context.setMixInAnnotations(Leaderboard.class, LeaderboardMixin.class);
		context.setMixInAnnotations(LeaderboardItem.class, LeaderboardItemMixin.class);
		context.setMixInAnnotations(Location.class, LocationMixin.class);
		context.setMixInAnnotations(MayorshipInfo.class, MayorshipInfoMixin.class);
		context.setMixInAnnotations(Photo.class, PhotoMixin.class);
		context.setMixInAnnotations(Photos.class, VenuePhotosMixin.class);
		context.setMixInAnnotations(PhotoGroups.class, PhotosMixin.class);
		context.setMixInAnnotations(PhotoGroup.class, PhotoGroupMixin.class);
		context.setMixInAnnotations(PhotoSize.class, PhotoSizeMixin.class);
		context.setMixInAnnotations(PhotoSizes.class, PhotoSizesMixin.class);
		context.setMixInAnnotations(PhotoSource.class, PhotoSourceMixin.class);
		context.setMixInAnnotations(Provider.class, ProviderMixin.class);
		context.setMixInAnnotations(Reason.class, ReasonMixin.class);
		context.setMixInAnnotations(Reasons.class, ReasonsMixin.class);
		context.setMixInAnnotations(Scores.class, ScoresMixin.class);
		context.setMixInAnnotations(Special.class, SpecialMixin.class);
		context.setMixInAnnotations(Specials.class, SpecialsMixin.class);
		context.setMixInAnnotations(Tip.class, TipMixin.class);
		context.setMixInAnnotations(TipGroup.class, TipGroupMixin.class);
		context.setMixInAnnotations(TipTodoGroup.class, TipTodoGroupMixin.class);
		context.setMixInAnnotations(TipUserGroup.class, TipUserGroupMixin.class);
		context.setMixInAnnotations(Tips.class, TipsMixin.class);
		context.setMixInAnnotations(Todo.class, TodoMixin.class);
		context.setMixInAnnotations(Todos.class, TodosMixin.class);
		context.setMixInAnnotations(UserSearchResponse.class, UserSearchResponseMixin.class);
		context.setMixInAnnotations(Venue.class, VenueMixin.class);
		context.setMixInAnnotations(VenueGroup.class, VenueGroupMixin.class);
		context.setMixInAnnotations(VenueGroupItem.class, VenueGroupItemMixin.class);
		context.setMixInAnnotations(VenueHistory.class, VenueHistoryMixin.class);
		context.setMixInAnnotations(VenueHistoryItem.class, VenueHistoryItemMixin.class);
		context.setMixInAnnotations(VenueLink.class, VenueLinkMixin.class);
		context.setMixInAnnotations(VenueLinks.class, VenueLinksMixin.class);
		context.setMixInAnnotations(VenueMayor.class, VenueMayorMixin.class);
		context.setMixInAnnotations(VenueStats.class, VenueStatsMixin.class);
		context.setMixInAnnotations(VenueTips.class, VenueTipsMixin.class);
	}
}
