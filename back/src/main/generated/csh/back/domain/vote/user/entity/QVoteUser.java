package csh.back.domain.vote.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QVoteUser is a Querydsl query type for VoteUser
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVoteUser extends EntityPathBase<VoteUser> {

    private static final long serialVersionUID = 1951597576L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QVoteUser voteUser = new QVoteUser("voteUser");

    public final csh.back.global.entity.QBaseEntity _super = new csh.back.global.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final csh.back.domain.trip.member.entity.QTripMember tripMember;

    public final NumberPath<Integer> updateCount = createNumber("updateCount", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final csh.back.domain.vote.vote.entity.QVote vote;

    public final csh.back.domain.vote.item.entity.QVoteItem voteItem;

    public QVoteUser(String variable) {
        this(VoteUser.class, forVariable(variable), INITS);
    }

    public QVoteUser(Path<? extends VoteUser> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QVoteUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QVoteUser(PathMetadata metadata, PathInits inits) {
        this(VoteUser.class, metadata, inits);
    }

    public QVoteUser(Class<? extends VoteUser> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.tripMember = inits.isInitialized("tripMember") ? new csh.back.domain.trip.member.entity.QTripMember(forProperty("tripMember"), inits.get("tripMember")) : null;
        this.vote = inits.isInitialized("vote") ? new csh.back.domain.vote.vote.entity.QVote(forProperty("vote"), inits.get("vote")) : null;
        this.voteItem = inits.isInitialized("voteItem") ? new csh.back.domain.vote.item.entity.QVoteItem(forProperty("voteItem"), inits.get("voteItem")) : null;
    }

}

