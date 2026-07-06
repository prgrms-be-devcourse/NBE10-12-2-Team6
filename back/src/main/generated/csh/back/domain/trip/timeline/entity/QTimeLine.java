package csh.back.domain.trip.timeline.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTimeLine is a Querydsl query type for TimeLine
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTimeLine extends EntityPathBase<TimeLine> {

    private static final long serialVersionUID = -367508305L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTimeLine timeLine = new QTimeLine("timeLine");

    public final csh.back.global.entity.QBaseEntity _super = new csh.back.global.entity.QBaseEntity(this);

    public final csh.back.domain.trip.place.entity.QTripPlace confirmedPlace;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Integer> dayNumber = createNumber("dayNumber", Integer.class);

    public final DateTimePath<java.time.LocalDateTime> endTime = createDateTime("endTime", java.time.LocalDateTime.class);

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final DateTimePath<java.time.LocalDateTime> startTime = createDateTime("startTime", java.time.LocalDateTime.class);

    public final csh.back.domain.trip.group.entity.QTripGroup tripGroup;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QTimeLine(String variable) {
        this(TimeLine.class, forVariable(variable), INITS);
    }

    public QTimeLine(Path<? extends TimeLine> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTimeLine(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTimeLine(PathMetadata metadata, PathInits inits) {
        this(TimeLine.class, metadata, inits);
    }

    public QTimeLine(Class<? extends TimeLine> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.confirmedPlace = inits.isInitialized("confirmedPlace") ? new csh.back.domain.trip.place.entity.QTripPlace(forProperty("confirmedPlace"), inits.get("confirmedPlace")) : null;
        this.tripGroup = inits.isInitialized("tripGroup") ? new csh.back.domain.trip.group.entity.QTripGroup(forProperty("tripGroup"), inits.get("tripGroup")) : null;
    }

}

