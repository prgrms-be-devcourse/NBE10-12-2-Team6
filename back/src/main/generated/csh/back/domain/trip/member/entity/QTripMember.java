package csh.back.domain.trip.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTripMember is a Querydsl query type for TripMember
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTripMember extends EntityPathBase<TripMember> {

    private static final long serialVersionUID = 1357617670L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTripMember tripMember = new QTripMember("tripMember");

    public final csh.back.global.entity.QBaseEntity _super = new csh.back.global.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final BooleanPath isAdmin = createBoolean("isAdmin");

    public final csh.back.domain.member.entity.QMember member;

    public final csh.back.domain.trip.group.entity.QTripGroup tripGroup;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QTripMember(String variable) {
        this(TripMember.class, forVariable(variable), INITS);
    }

    public QTripMember(Path<? extends TripMember> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTripMember(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTripMember(PathMetadata metadata, PathInits inits) {
        this(TripMember.class, metadata, inits);
    }

    public QTripMember(Class<? extends TripMember> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new csh.back.domain.member.entity.QMember(forProperty("member")) : null;
        this.tripGroup = inits.isInitialized("tripGroup") ? new csh.back.domain.trip.group.entity.QTripGroup(forProperty("tripGroup"), inits.get("tripGroup")) : null;
    }

}

