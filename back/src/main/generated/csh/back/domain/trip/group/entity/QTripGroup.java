package csh.back.domain.trip.group.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTripGroup is a Querydsl query type for TripGroup
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTripGroup extends EntityPathBase<TripGroup> {

    private static final long serialVersionUID = 269150894L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTripGroup tripGroup = new QTripGroup("tripGroup");

    public final csh.back.global.entity.QBaseEntity _super = new csh.back.global.entity.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final DatePath<java.time.LocalDate> endDate = createDate("endDate", java.time.LocalDate.class);

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final BooleanPath isVote = createBoolean("isVote");

    public final StringPath joinCode = createString("joinCode");

    public final StringPath name = createString("name");

    public final NumberPath<Integer> nights = createNumber("nights", Integer.class);

    public final csh.back.domain.member.entity.QMember owner;

    public final StringPath region = createString("region");

    public final DatePath<java.time.LocalDate> startDate = createDate("startDate", java.time.LocalDate.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QTripGroup(String variable) {
        this(TripGroup.class, forVariable(variable), INITS);
    }

    public QTripGroup(Path<? extends TripGroup> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTripGroup(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTripGroup(PathMetadata metadata, PathInits inits) {
        this(TripGroup.class, metadata, inits);
    }

    public QTripGroup(Class<? extends TripGroup> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.owner = inits.isInitialized("owner") ? new csh.back.domain.member.entity.QMember(forProperty("owner")) : null;
    }

}

