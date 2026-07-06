package csh.back.domain.trip.place.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTripPlace is a Querydsl query type for TripPlace
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTripPlace extends EntityPathBase<TripPlace> {

    private static final long serialVersionUID = -326415442L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTripPlace tripPlace = new QTripPlace("tripPlace");

    public final csh.back.global.entity.QBaseEntity _super = new csh.back.global.entity.QBaseEntity(this);

    public final StringPath address = createString("address");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final csh.back.domain.trip.member.entity.QTripMember createdBy;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final StringPath kakaoMapUrl = createString("kakaoMapUrl");

    public final StringPath kakaoPlaceId = createString("kakaoPlaceId");

    public final StringPath name = createString("name");

    public final StringPath theme = createString("theme");

    public final csh.back.domain.trip.group.entity.QTripGroup tripGroup;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QTripPlace(String variable) {
        this(TripPlace.class, forVariable(variable), INITS);
    }

    public QTripPlace(Path<? extends TripPlace> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTripPlace(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTripPlace(PathMetadata metadata, PathInits inits) {
        this(TripPlace.class, metadata, inits);
    }

    public QTripPlace(Class<? extends TripPlace> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.createdBy = inits.isInitialized("createdBy") ? new csh.back.domain.trip.member.entity.QTripMember(forProperty("createdBy"), inits.get("createdBy")) : null;
        this.tripGroup = inits.isInitialized("tripGroup") ? new csh.back.domain.trip.group.entity.QTripGroup(forProperty("tripGroup"), inits.get("tripGroup")) : null;
    }

}

