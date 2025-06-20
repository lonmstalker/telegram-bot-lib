package io.lonmstalker.tgkit.doc.mapper;

import io.lonmstalker.tgkit.doc.scraper.MethodDoc;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Маппер между {@link MethodDoc} и {@link OperationInfo}.
 */
@Mapper
public interface MethodDocMapper {
  MethodDocMapper INSTANCE = Mappers.getMapper(MethodDocMapper.class);

  OperationInfo toOperation(MethodDoc doc);
}
