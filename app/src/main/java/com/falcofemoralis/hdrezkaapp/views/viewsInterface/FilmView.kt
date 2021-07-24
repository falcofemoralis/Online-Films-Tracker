package com.falcofemoralis.hdrezkaapp.views.viewsInterface

import com.falcofemoralis.hdrezkaapp.interfaces.IConnection
import com.falcofemoralis.hdrezkaapp.interfaces.IOnBackPressed
import com.falcofemoralis.hdrezkaapp.objects.*
import com.falcofemoralis.hdrezkaapp.views.elements.CommentEditor

interface FilmView : IConnection, CommentEditor.ICommentEditor {
    fun setFilmBaseData(film: Film)

    fun setActors(actors: ArrayList<Actor?>)

    fun setDirectors(directors: ArrayList<String>)

    fun setCountries(countries: ArrayList<String>)

    fun setGenres(genres: ArrayList<String>)

    fun setFullSizeImage(posterPath: String)

    fun setPlayer(link: String)

    fun setSchedule(schedule: ArrayList<Pair<String, ArrayList<Schedule>>>)

    fun setCollection(collection: ArrayList<Film>)

    fun setRelated(collection: ArrayList<Film>)

    fun setBookmarksList(bookmarks: ArrayList<Bookmark>)

    fun setCommentsList(list: ArrayList<Comment>, filmId: String)

    fun redrawComments()

    fun setCommentsProgressState(state: Boolean)

    fun setCommentEditor(filmId: String)

    fun setShareBtn(title: String, link: String)
}