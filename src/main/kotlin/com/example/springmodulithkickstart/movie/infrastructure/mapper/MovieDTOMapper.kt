package com.example.springmodulithkickstart.movie.infrastructure.mapper

import com.example.springmodulithkickstart.movie.api.dto.MovieDTO
import com.example.springmodulithkickstart.movie.infrastructure.db.Movie
import org.springframework.stereotype.Component
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.memberProperties

@Component
class MovieDTOMapper {
    /*
    maps an object to another type of object based on equal field names.
    Notice that this only works if the field names to map are equal.
     */
    fun <O, M : Any> mapBasedOnPropertyNames(oldObject : O, mappedClass : KClass<M>) : M {
        val mappedInstance = mappedClass.createInstance()
        for(property in oldObject!!::class.memberProperties) {
            val value = property.getter.call(oldObject)
            val prop = mappedClass.members.find { it.name == property.name }
            if(prop is KMutableProperty<*>) {
                prop.setter.call(mappedInstance, value)
            }
        }
        return mappedInstance
    }
    fun mapToMovieDTO(movie: Movie): MovieDTO {
        return mapBasedOnPropertyNames(movie, MovieDTO::class)
    }
}