package me.farahani.marginitemdecorator

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.navigation.NavigationView

class ShowcaseActivity : AppCompatActivity() {

    companion object {
        const val ITEM_SIZE = 120//dp
        const val MARGIN = 56//dp
    }

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_showcase)
        drawer = findViewById(R.id.drawer_layout)

        toggle = ActionBarDrawerToggle(
            this, drawer,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        recyclerView = RecyclerView(this)
        addRecyclerViewTo(recyclerView, findViewById(R.id.rv_container))
        updateRecyclerView()

        val navigationView: NavigationView = findViewById(R.id.navigation_view)
        setConfigListeners(navigationView)
    }


    private fun setConfigListeners(navigationView: NavigationView) {
        with(navigationView.menu) {
            setupUpdateButton()
            setupRtlSwitch()
            setupIncludeEdge()
            setupOrientationSpinner()
            setupSpansSpinner()
            setupLayoutManagerSpinner()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun Menu.setupUpdateButton() {
        (findItem(R.id.update).actionView as AppCompatButton).also {
            it.text = "Update"
            it.setOnClickListener {
                drawer.close()
                updateRecyclerView()
            }
        }
    }

    private fun Menu.setupRtlSwitch() {
        (findItem(R.id.rtl).actionView as SwitchCompat).setOnCheckedChangeListener { _, isChecked ->
            Config.rtl = isChecked
        }
    }

    private fun Menu.setupIncludeEdge() {
        (findItem(R.id.edge).actionView as SwitchCompat).apply {
            isChecked = true
        }.setOnCheckedChangeListener { _, isChecked ->
            Config.includeEdge = isChecked
        }
    }

    private fun Menu.setupOrientationSpinner() {
        (findItem(R.id.orientation).actionView as AppCompatSpinner).also { spinner ->
            spinner.adapter = ArrayAdapter(
                this@ShowcaseActivity,
                android.R.layout.simple_spinner_item,
                Orientation.values()
            ).also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    Config.orientation =
                        (parent.getItemAtPosition(position) as Orientation).orientation
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    private fun Menu.setupSpansSpinner() {
        (findItem(R.id.spans).actionView as AppCompatSpinner).also { spinner ->
            spinner.adapter = ArrayAdapter(
                this@ShowcaseActivity,
                android.R.layout.simple_spinner_item,
                listOf(1, 2, 3)
            ).also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val newSpans = (parent.getItemAtPosition(position) as Int)
                    Config.spans = newSpans
                    if (newSpans >= 2)
                        (findItem(R.id.manager).actionView as AppCompatSpinner).also { spinner ->
                            spinner.setSelection(1/*grid*/)
                        }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    private fun Menu.setupLayoutManagerSpinner() {
        (findItem(R.id.manager).actionView as AppCompatSpinner).also { spinner ->
            spinner.adapter = ArrayAdapter(
                this@ShowcaseActivity,
                android.R.layout.simple_spinner_item,
                listOf("linear", "grid")
            ).also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    Config.layoutManager =
                        (parent.getItemAtPosition(position) as String)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    private fun addRecyclerViewTo(recyclerView: RecyclerView, frameLayout: FrameLayout) {
        frameLayout.addView(recyclerView, MATCH_PARENT, MATCH_PARENT)
    }

    private fun updateRecyclerView() {
        recyclerView.apply {
            adapter = createRecyclerViewAdapter()
            layoutManager = Config.getLayoutManager(context)
            layoutDirection = Config.getLayoutDirection()
            if (itemDecorationCount == 1)
                removeItemDecorationAt(0)
            addItemDecoration(
                MarginItemDecorator(
                    dpToPx(MARGIN),
                    Config.includeEdge
                )
            )
        }
    }

    private fun createRecyclerViewAdapter(): RecyclerView.Adapter<IndexViewHolder> {
        return object : RecyclerView.Adapter<IndexViewHolder>() {
            var items = List(11) { it }

            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): IndexViewHolder {
                val tv = AppCompatTextView(this@ShowcaseActivity)
                val width = when (Config.orientation) {
                    VERTICAL -> MATCH_PARENT
                    else -> dpToPx(ITEM_SIZE)
                }
                val height = when (Config.orientation) {
                    HORIZONTAL -> MATCH_PARENT
                    else -> dpToPx(ITEM_SIZE)
                }

                tv.layoutParams = ViewGroup.LayoutParams(width, height)
                tv.setBackgroundColor(Color.GRAY)
                tv.setTextColor(Color.BLACK)
                tv.textSize = 24f /*dp*/
                tv.gravity = Gravity.CENTER
                return IndexViewHolder(tv)
            }

            override fun onBindViewHolder(holder: IndexViewHolder, position: Int) {
                holder.tv.text = position.toString()
            }

            override fun getItemCount() = items.size
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}

enum class Orientation(val orientation: Int) {
    Vertical(VERTICAL),
    Horizontal(HORIZONTAL)
}

object Config {
    var includeEdge: Boolean = true
    var layoutManager: String = "linear"
    var orientation: Int = VERTICAL
    var rtl: Boolean = false
    var spans: Int = 1
    fun getLayoutManager(context: Context): RecyclerView.LayoutManager {
        return when (layoutManager) {
            "linear" -> LinearLayoutManager(context, orientation, false)
            "grid" -> GridLayoutManager(context, spans, orientation, false)
            else -> throw IllegalArgumentException("Invalid layout")
        }
    }

    fun getLayoutDirection(): Int {
        return if (rtl) ViewCompat.LAYOUT_DIRECTION_RTL else ViewCompat.LAYOUT_DIRECTION_LTR
    }
}

class IndexViewHolder(val tv: AppCompatTextView) : RecyclerView.ViewHolder(tv)

fun Context.dpToPx(dp: Int) = (resources.displayMetrics.density * dp).toInt()