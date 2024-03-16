package com.shop.entity.api.todo;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTodoEntity is a Querydsl query type for TodoEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTodoEntity extends EntityPathBase<TodoEntity> {

    private static final long serialVersionUID = 616327613L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTodoEntity todoEntity = new QTodoEntity("todoEntity");

    public final com.shop.entity.base.QBaseEntity _super = new com.shop.entity.base.QBaseEntity(this);

    //inherited
    public final StringPath createdBy = _super.createdBy;

    public final BooleanPath done = createBoolean("done");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.shop.entity.QMember member;

    //inherited
    public final StringPath modifiedBy = _super.modifiedBy;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> regTime = _super.regTime;

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updateTime = _super.updateTime;

    public QTodoEntity(String variable) {
        this(TodoEntity.class, forVariable(variable), INITS);
    }

    public QTodoEntity(Path<? extends TodoEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTodoEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTodoEntity(PathMetadata metadata, PathInits inits) {
        this(TodoEntity.class, metadata, inits);
    }

    public QTodoEntity(Class<? extends TodoEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new com.shop.entity.QMember(forProperty("member")) : null;
    }

}

