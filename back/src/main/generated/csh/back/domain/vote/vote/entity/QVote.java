package csh.back.domain.vote.vote.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QVote is a Querydsl query type for Vote
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QVote extends EntityPathBase<Vote> {

    private static final long serialVersionUID = -449909956L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QVote vote = new QVote("vote");

    public final csh.back.global.entity.QBaseEntity _super = new csh.back.global.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final DateTimePath<java.time.LocalDateTime> expireTime = createDateTime("expireTime", java.time.LocalDateTime.class);

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final BooleanPath isConfirmed = createBoolean("isConfirmed");

    public final csh.back.domain.trip.timeline.entity.QTimeLine timeLine;

    public final csh.back.domain.trip.group.entity.QTripGroup tripGroup;

    public final csh.back.domain.trip.member.entity.QTripMember tripMember;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QVote(String variable) {
        this(Vote.class, forVariable(variable), INITS);
    }

    public QVote(Path<? extends Vote> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QVote(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QVote(PathMetadata metadata, PathInits inits) {
        this(Vote.class, metadata, inits);
    }

    public QVote(Class<? extends Vote> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.timeLine = inits.isInitialized("timeLine") ? new csh.back.domain.trip.timeline.entity.QTimeLine(forProperty("timeLine"), inits.get("timeLine")) : null;
        this.tripGroup = inits.isInitialized("tripGroup") ? new csh.back.domain.trip.group.entity.QTripGroup(forProperty("tripGroup"), inits.get("tripGroup")) : null;
        this.tripMember = inits.isInitialized("tripMember") ? new csh.back.domain.trip.member.entity.QTripMember(forProperty("tripMember"), inits.get("tripMember")) : null;
    }

}

