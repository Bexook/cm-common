package com.cm.common.mapper;

import lombok.SneakyThrows;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class OrikaBeanMapper {

    @Autowired
    private MapperFactory mapperFactory;

    @SneakyThrows
    public <R, T> R map(T sourceObj, Class<R> r) {
        if (Objects.isNull(sourceObj)) {
            return null;
        }
        mapperFactory.classMap(Class.forName(sourceObj.getClass().getName()), r);
        MapperFacade mapperFacade = mapperFactory.getMapperFacade();
        return mapperFacade.map(sourceObj, r);
    }

    public <R, T> List<R> mapAsList(Collection<T> sourceCollection, Class<R> r) {
        return sourceCollection.stream().map(s -> map(s, r)).collect(Collectors.toList());
    }


    public <R, T> Set<R> mapAsSet(Collection<T> sourceCollection, Class<R> r) {
        return sourceCollection.stream().map(s -> map(s, r)).collect(Collectors.toSet());
    }
}
