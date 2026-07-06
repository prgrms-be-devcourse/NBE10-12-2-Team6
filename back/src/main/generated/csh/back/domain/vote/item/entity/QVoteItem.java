package csh.back.domain.vote.item.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QVoteItem is a Querydsl query type for VoteItem
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVoteItem extends EntityPathBase<VoteItem> {

    private static final long serialVersionUID = -753888616L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QVoteItem voteItem = new QVoteItem("voteItem");

    public final csh.back.global.entity.QBaseEntity _super = new csh.back.global.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final csh.back.domain.trip.place.entity.QTripPlace tripPlace;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final csh.back.domain.vote.vote.entity.QVote vote;

    public QVoteItem(String variable) {
        this(VoteItem.class, forVariable(variable), INITS);
    }

    public QVoteItem(Path<? extends VoteItem> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QVoteItem(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QVoteItem(PathMetadata metadata, PathInits inits) {
        this(VoteItem.class, metadata, inits);
    }

    public QVoteItem(Class<? extends VoteItem> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.tripPlace = inits.isInitialized("tripPlace") ? new csh.back.domain.trip.place.entity.QTripPlace(forProperty("tripPlace"), inits.get("tripPlace")) : null;
        this.vote = inits.isInitialized("vote") ? new csh.back.domain.vote.vote.entity.QVote(forProperty("vote"), inits.get("vote")) : null;
    }

}

