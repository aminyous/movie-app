package com.movieflix.movieApi.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieflix.movieApi.dto.MovieDto;
import com.movieflix.movieApi.dto.MoviePageResponse;
import com.movieflix.movieApi.exception.EmptyFileException;
import com.movieflix.movieApi.service.MovieService;
import com.movieflix.movieApi.utils.AppConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.Inet4Address;
import java.util.List;

@RestController
@RequestMapping("/api/v1/movie")
public class MovieController {
    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }


    @PostMapping("add-movie")
    public ResponseEntity<MovieDto> addMovieHandler(@RequestPart MultipartFile file,
                                                    @RequestPart String movieDto) throws IOException, EmptyFileException {
        if(file.isEmpty()){
            throw new EmptyFileException("File is empty! Please send another file.");
        }
        MovieDto dto = convertToMovieDto(movieDto);
        return new ResponseEntity<>(movieService.addMovie(dto, file), HttpStatus.CREATED);
    }

    @GetMapping("{movieId}")
    public ResponseEntity<MovieDto> getMovieHandler(@PathVariable Integer movieId){

        return new ResponseEntity<>(movieService.getMovie(movieId), HttpStatus.OK);

    }

    @GetMapping("all")
    public ResponseEntity<List<MovieDto>> getAllMoviesHandler(){
        return ResponseEntity.ok(movieService.getAllMovies());
    }



    @PutMapping("/update/{movieId}")
    public ResponseEntity<MovieDto> updateMoviehandler(
            @PathVariable Integer movieId,
            @RequestPart MultipartFile file,
            @RequestPart String movieDtoObject) throws IOException {

        if (file.isEmpty()) file = null;

        MovieDto dto = convertToMovieDto(movieDtoObject);

        return ResponseEntity.ok(movieService.updateMovie(movieId, dto, file));


    }

    @DeleteMapping("/delete/{movieId}")
    public ResponseEntity<String> deleteMovieHandler(@PathVariable Integer movieId) throws IOException {

        return ResponseEntity.ok(movieService.deleteMovie(movieId));
    }

    private MovieDto convertToMovieDto(String movieDtoObject) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        return  objectMapper.readValue(movieDtoObject, MovieDto.class);
    }

    @GetMapping("/allMoviePage")
    public ResponseEntity<MoviePageResponse> getMoviesWithPagination(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize
            ){
        return new ResponseEntity<>(movieService.getAllMoviesWithPagination(pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping("/allMoviePageSort")
    public ResponseEntity<MoviePageResponse> getMoviesWithPaginationWithSorting(
            @RequestParam(defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
            @RequestParam(defaultValue = AppConstants.SORT_DIR, required = false) String dir
    ){
        return new ResponseEntity<>(movieService.getAllMoviesWithPaginationAndSorting(pageNumber, pageSize, sortBy, dir), HttpStatus.OK);
    }
}
