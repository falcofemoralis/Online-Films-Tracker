package com.BSLCommunity.onlinefilmstracker.presenters

import com.BSLCommunity.onlinefilmstracker.constants.BookmarkFilterType
import com.BSLCommunity.onlinefilmstracker.models.BookmarksModel
import com.BSLCommunity.onlinefilmstracker.models.FilmModel
import com.BSLCommunity.onlinefilmstracker.objects.Bookmark
import com.BSLCommunity.onlinefilmstracker.objects.Film
import com.BSLCommunity.onlinefilmstracker.interfaces.IMsg
import com.BSLCommunity.onlinefilmstracker.views.viewsInterface.BookmarksView
import com.BSLCommunity.onlinefilmstracker.views.viewsInterface.FilmsListView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookmarksPresenter(private val bookmarksView: BookmarksView, private val filmsListView: FilmsListView) {
    private val FILMS_PER_PAGE = 9

    var bookmarks: ArrayList<Bookmark>? = null
    val sortFilters: ArrayList<String> = ArrayList(arrayListOf("added", "year", "popular"))
    val showFilters: ArrayList<String> = ArrayList(arrayListOf("0", "1", "2", "3", "82"))
    private var selectedBookmark: Bookmark? = null
    private var selectedSortFilter: String? = null
    private var selectedShowFilter: String? = null
    private var curPage = 1
    private var loadedFilms: ArrayList<Film> = ArrayList()
    private var activeFilms: ArrayList<Film> = ArrayList()
    private var isLoading: Boolean = false

    fun initBookmarks() {
        GlobalScope.launch {
            bookmarks = BookmarksModel.getBookmarksList()

            withContext(Dispatchers.Main) {
                if (bookmarks != null && bookmarks!!.size > 0) {
                    val names: ArrayList<String> = ArrayList()
                    for (bookmark in bookmarks!!) {
                        names.add("${bookmark.name} (${bookmark.amount})")
                    }
                    bookmarksView.setBookmarksSpinner(names)
                    filmsListView.setFilms(activeFilms)
                } else{
                    bookmarksView.setNoBookmarks()
                }
            }
        }
    }

    fun setBookmark(bookmark: Bookmark) {
        reset()
        selectedBookmark = bookmark
        getNextFilms()
    }

    private fun reset() {
        curPage = 1
        activeFilms.clear()
        loadedFilms.clear()
    }

    fun getNextFilms() {
        if (isLoading) {
            return
        }

        isLoading = true
        if (loadedFilms.size > 0) {
            FilmModel.getFilmsData(loadedFilms, FILMS_PER_PAGE, ::addFilms)
        } else {
            GlobalScope.launch {
                // if page is not empty
                selectedBookmark?.let {
                    loadedFilms.addAll(BookmarksModel.getFilmsFromBookmarkPage(it.link, curPage++, selectedSortFilter, selectedShowFilter))
                }

                if (loadedFilms.size > 0) {
                    FilmModel.getFilmsData(loadedFilms, FILMS_PER_PAGE, ::addFilms)
                } else if (curPage == 2) {
                    isLoading = false
                    withContext(Dispatchers.Main) {
                        bookmarksView.showMsg(IMsg.MsgType.NOTHING_FOUND)
                    }
                } else {
                    isLoading = false
                    withContext(Dispatchers.Main) {
                        filmsListView.setProgressBarState(false)
                    }
                }
            }
        }
    }

    private fun addFilms(films: ArrayList<Film>) {
        isLoading = false
        activeFilms.addAll(films)
        filmsListView.redrawFilms()
        filmsListView.setProgressBarState(false)
    }

    fun setFilter(filter: String, type: BookmarkFilterType) {
        when (type) {
            BookmarkFilterType.SORT -> selectedSortFilter = filter
            BookmarkFilterType.SHOW -> selectedShowFilter = filter
        }

        reset()
        getNextFilms()
    }

    fun setMsg(type: IMsg.MsgType) {
        filmsListView.setProgressBarState(false)
        bookmarksView.showMsg(type)
    }
}