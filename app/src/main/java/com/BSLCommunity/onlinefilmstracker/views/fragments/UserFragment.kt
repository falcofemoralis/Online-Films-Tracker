package com.BSLCommunity.onlinefilmstracker.views.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebView
import android.widget.*
import androidx.fragment.app.Fragment
import com.BSLCommunity.onlinefilmstracker.R
import com.BSLCommunity.onlinefilmstracker.clients.AuthWebViewClient
import com.BSLCommunity.onlinefilmstracker.models.BookmarksModel
import com.BSLCommunity.onlinefilmstracker.models.UserModel
import com.BSLCommunity.onlinefilmstracker.presenters.UserPresenter

class UserFragment : Fragment() {
    private lateinit var currentView: View
    private lateinit var userPresenter: UserPresenter
    private lateinit var popupWindowView: RelativeLayout
    private lateinit var popupWindow: PopupWindow
    private lateinit var webView: WebView
    private lateinit var popupWindowLoadingBar: ProgressBar
    private var popupWindowCloseBtn: Button? = null
    private var isLoaded = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        currentView = inflater.inflate(R.layout.fragment_user, container, false)
        userPresenter = UserPresenter()
        Log.d("FRAGMENT_TEST", "user init")

        currentView.findViewById<TextView>(R.id.fragment_user_tv_exit).setOnClickListener {
            activity?.let {
                UserModel.saveLoggedIn(false, it)
                setAuthPanel(false)
                CookieManager.getInstance().removeAllCookies(null)
                CookieManager.getInstance().flush()
            }
        }

        UserModel.isLoggedIn?.let { setAuthPanel(it) }

        return currentView
    }

    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    private fun createAuthDialog() {
        activity?.let {
            popupWindowView = requireActivity().layoutInflater.inflate(R.layout.dialog_auth, null) as RelativeLayout
            popupWindow = PopupWindow(popupWindowView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true)
            popupWindowLoadingBar = popupWindowView.findViewById(R.id.dialog_auth_pb_loading)
            popupWindowCloseBtn = popupWindowView.findViewById(R.id.dialog_auth_bt_close)
            popupWindowView.setOnTouchListener { _, _ -> //Close the window when clicked outside
                popupWindow.dismiss()
                true
            }
            popupWindowView.findViewById<Button>(R.id.dialog_auth_bt_close).setOnClickListener {
                popupWindow.dismiss()
            }

            webView = popupWindowView.findViewById(R.id.dialog_auth_wv_auth)
            webView.settings.javaScriptEnabled = true
            webView.settings.domStorageEnabled = true

            currentView.findViewById<TextView>(R.id.fragment_user_tv_login).setOnClickListener {
                // login
                loadAuthPage(true)
            }

            currentView.findViewById<TextView>(R.id.fragment_user_tv_register).setOnClickListener {
                //register
                loadAuthPage(false)
            }
        }
    }

    private fun loadAuthPage(isLogin: Boolean) {
        webView.visibility = View.GONE
        popupWindowCloseBtn?.visibility = View.GONE
        popupWindowLoadingBar.visibility = View.VISIBLE

        webView.webViewClient = AuthWebViewClient(isLogin, ::authCallback)
        popupWindow.showAtLocation(popupWindowView, Gravity.CENTER, 0, 0)
        if (!isLoaded) {
            webView.loadUrl(BookmarksModel.MAIN_PAGE)
            isLoaded = true
        }
    }

    private fun authCallback(isLogged: Boolean) {
        webView.stopLoading()
        isLoaded = false

        if (isLogged) {
            UserModel.saveLoggedIn(isLogged, requireContext())
            setAuthPanel(isLogged)
            webView.visibility = View.GONE
        } else {
            popupWindowLoadingBar.visibility = View.GONE
            popupWindowCloseBtn?.visibility = View.VISIBLE
            webView.visibility = View.VISIBLE
        }
    }

    private fun setAuthPanel(isLogged: Boolean) {
        if (isLogged) {
            popupWindowCloseBtn?.visibility = View.GONE
            currentView.findViewById<LinearLayout>(R.id.fragment_user_ll_auth_panel).visibility = View.GONE
            currentView.findViewById<TextView>(R.id.fragment_user_tv_exit).visibility = View.VISIBLE
        } else {
            currentView.findViewById<LinearLayout>(R.id.fragment_user_ll_auth_panel).visibility = View.VISIBLE
            currentView.findViewById<TextView>(R.id.fragment_user_tv_exit).visibility = View.GONE

            createAuthDialog()

        }
    }
}