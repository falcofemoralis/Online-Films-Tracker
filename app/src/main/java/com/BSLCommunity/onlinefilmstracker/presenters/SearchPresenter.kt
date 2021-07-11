package com.BSLCommunity.onlinefilmstracker.presenters

import com.BSLCommunity.onlinefilmstracker.models.FilmModel
import com.BSLCommunity.onlinefilmstracker.models.FilmsListModel
import com.BSLCommunity.onlinefilmstracker.models.SearchModel
import com.BSLCommunity.onlinefilmstracker.objects.Film
import com.BSLCommunity.onlinefilmstracker.viewsInterface.FilmsListView
import com.BSLCommunity.onlinefilmstracker.viewsInterface.SearchView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchPresenter(private val searchView: SearchView, private val filmsListView: FilmsListView) {
    private val FILMS_PER_PAGE = 9
    var activeSearchFilms: ArrayList<Film> = ArrayList()
    var activeListFilms: ArrayList<Film> = ArrayList()
    private var loadedListFilms: ArrayList<Film> = ArrayList()
    private var currentPage: Int = 1
    private var isLoading: Boolean = false
    private var query: String = ""

    fun initFilms() {
        filmsListView.setFilms(activeListFilms)
    }

    fun getFilms(text: String) {
        GlobalScope.launch {
            activeSearchFilms = SearchModel.getFilmsListByQuery(text)

            val searchFilms: ArrayList<String> = ArrayList()
            for (film in activeSearchFilms) {
                searchFilms.add("${film.title} ${film.additionalInfo} ${film.ratingIMDB}")
            }

            withContext(Dispatchers.Main) {
                searchView.redrawSearchFilms(searchFilms)
            }
        }
    }

    fun setQuery(text: String) {
        query = text
        getNextFilms()
    }

    fun getNextFilms() {
        if (isLoading) {
            return
        }

        isLoading = true
        filmsListView.setProgressBarState(true)

        GlobalScope.launch {
            if (loadedListFilms.size == 0) {
                loadedListFilms = FilmsListModel.getFilmsFromPage(SearchModel.SEARCH_QUERY + query + "&page=$currentPage")
                currentPage++
            }
            FilmModel.getFilmsData(loadedListFilms, FILMS_PER_PAGE, ::addFilms)
        }
    }


    private fun addFilms(films: ArrayList<Film>) {
        isLoading = false
        activeListFilms.addAll(films)
        filmsListView.redrawFilms()
        filmsListView.setProgressBarState(false)
    }
}