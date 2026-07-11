package com.example.springmodulithkickstart.movie.infrastructure.mapper

import com.example.springmodulithkickstart.movie.api.dto.MovieDto
import com.example.springmodulithkickstart.movie.infrastructure.db.Movie
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MovieDtoMapperMockKTest {

    private val mapper = MovieDtoMapper()

    @Test
    fun `mapToMovieDTO should map all matching properties from Movie to MovieDto`() {
        val movie = Movie().apply {
            movieId = "test-id-123"
            title = "Inception"
            description = "A mind-bending thriller"
        }

        val dto: MovieDto = mapper.mapToMovieDTO(movie)

        assertThat(dto.title).isEqualTo("Inception")
        assertThat(dto.description).isEqualTo("A mind-bending thriller")
    }

    @Test
    fun `mapBasedOnPropertyNames should map properties with matching names`() {
        val source = SimpleSource(name = "Alice", age = 30)

        val target: SimpleTarget = mapper.mapBasedOnPropertyNames(source, SimpleTarget::class)

        assertThat(target.name).isEqualTo("Alice")
        assertThat(target.age).isEqualTo(30)
    }

    @Test
    fun `mapBasedOnPropertyNames should skip non-matching properties`() {
        val source = SimpleSource(name = "Bob", age = 25)
        source.extra = "should-not-appear"

        val target: SimpleTarget = mapper.mapBasedOnPropertyNames(source, SimpleTarget::class)

        assertThat(target.name).isEqualTo("Bob")
        assertThat(target.age).isEqualTo(25)
        // extra does not exist on SimpleTarget → should be ignored
    }

    @Test
    fun `mapBasedOnPropertyNames should skip read-only properties on target`() {
        val source = SourceWithReadOnly(name = "Charlie", age = 35)

        val target: TargetWithReadOnly = mapper.mapBasedOnPropertyNames(source, TargetWithReadOnly::class)

        assertThat(target.name).isEqualTo("Charlie")
        assertThat(target.readOnlyLabel).isNull()
    }

    @Test
    fun `spy should verify mapBasedOnPropertyNames is called internally`() {
        val spy = spyk(mapper)
        val movie = Movie().apply {
            title = "Interstellar"
            description = "Space exploration"
        }

        spy.mapToMovieDTO(movie)

        verify { spy.mapBasedOnPropertyNames(movie, MovieDto::class) }
    }

    @Test
    fun `mockk mock as source should be handled by reflection`() {
        val mockedSource = mockk<SimpleSource> {
            every { name } returns "MockedName"
            every { age } returns 99
            every { extra } returns null
        }

        val target: SimpleTarget = mapper.mapBasedOnPropertyNames<SimpleSource, SimpleTarget>(mockedSource, SimpleTarget::class)

        assertThat(target.name).isEqualTo("MockedName")
        assertThat(target.age).isEqualTo(99)
    }

    class SimpleSource(var name: String? = null, var age: Int? = null) {
        var extra: String? = null
    }

    class SimpleTarget {
        var name: String? = null
        var age: Int? = null
    }

    class SourceWithReadOnly(var name: String? = null, var age: Int? = null)

    class TargetWithReadOnly {
        var name: String? = null
        val readOnlyLabel: String? = null
    }
}
