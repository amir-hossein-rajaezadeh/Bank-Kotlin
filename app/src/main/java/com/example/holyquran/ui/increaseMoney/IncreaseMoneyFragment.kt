package com.example.holyquran.ui.increaseMoney

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.holyquran.R
import com.example.holyquran.ViewModelProviderFactory
import com.example.holyquran.data.database.UserDatabase
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import com.example.holyquran.databinding.FragmentIncreaseMoneyBinding
import com.example.holyquran.utils.toCurrencyFormat
import java.lang.Exception
import java.text.NumberFormat
import java.text.DecimalFormat


class IncreaseMoneyFragment : Fragment() {
    var userId: Long = 0L
    var id2: Boolean = false
    var numbers = 1
    var decide = ""
    lateinit var mIncreaseMoneyBinding: FragmentIncreaseMoneyBinding
    lateinit var mIncreaseMoneyViewModel: IncreaseMoneyViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mIncreaseMoneyBinding =
            DataBindingUtil.inflate(
                layoutInflater,
                R.layout.fragment_increase_money,
                container,
                false
            )
        val application = requireNotNull(this.activity).application
        val userDAO = UserDatabase.getInstance(application).mUserDAO
        val transactionDAO = UserDatabase.getInstance(application).mTransactionsDAO
        val loanDAO = UserDatabase.getInstance(application).mLoanDAO
        val viewModelFactory =
            ViewModelProviderFactory(userDAO, transactionDAO, loanDAO, application)
        mIncreaseMoneyViewModel =
            ViewModelProviders.of(
                this, viewModelFactory
            ).get(IncreaseMoneyViewModel::class.java)
        mIncreaseMoneyBinding.increaseMoneyViewModel = mIncreaseMoneyViewModel
        this.also { mIncreaseMoneyBinding.lifecycleOwner = it }
        mIncreaseMoneyViewModel.increaseMoneyDone()

        val arg =
            IncreaseMoneyFragmentArgs.fromBundle(
                requireArguments()
            )
        userId = arg.userIdIncrease
        Log.d("TAG", "onCreateView: $userId")
        mIncreaseMoneyViewModel.setUserName(userId)?.observe(viewLifecycleOwner, {
            mIncreaseMoneyViewModel.setUserName(it)
        })
        mIncreaseMoneyViewModel.userName.observe(viewLifecycleOwner, {
            if (it != null) {
                mIncreaseMoneyBinding.userName = it
            }
        })
        val increase = mIncreaseMoneyViewModel.sumUserIncrease(userId).toLong()
        val decrease = mIncreaseMoneyViewModel.sumUserDecrease(userId).toLong()
        val result = increase - decrease
        mIncreaseMoneyBinding.totalMoney.text = NumberFormat.getInstance().format(result)
        mIncreaseMoneyViewModel.increaseMoney.observe(viewLifecycleOwner, Observer {
            val removeComma = mIncreaseMoneyBinding.increaseEdt.text.toString().replace(",", "")
            if (it == true) {
                if (id2 == true) {
                    mIncreaseMoneyViewModel.setLoanDetail(userId)?.observe(viewLifecycleOwner, {
                        if (mIncreaseMoneyBinding.increaseEdt.text.toString() != it.payment) {
                            val builder: AlertDialog.Builder =
                                AlertDialog.Builder(requireActivity())
                            builder.setIcon(R.drawable.warning)
                            val increaseEditText = mIncreaseMoneyBinding.increaseEdt.text.toString()
                            val currentPayment = it.payment

                            if (increaseEditText.toInt() > currentPayment.toInt()) {
                                val more = "بیشتر"
                                decide = more
                            } else {
                                val less = "کمتر"
                                decide = less
                            }
                            builder.setTitle(" مبلغ مورد نظر از مبلغ قسط وام $decide است. ادامه میدهید؟")
                                .setCancelable(false)
                                .setPositiveButton("اره به هر حال واریز کن",
                                    DialogInterface.OnClickListener { dialog, id ->
                                        mIncreaseMoneyViewModel.insertLoanPayments(
                                            removeComma,
                                            true,
                                            numbers.toString(),
                                            userId
                                        )
                                        Toast.makeText(
                                            activity,
                                            "قسط با موفقیت برداخت شد.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    })
                                .setNegativeButton("نه,ممنون",
                                    DialogInterface.OnClickListener { dialog, id -> dialog.dismiss() }
                                )
                            val alert: AlertDialog = builder.create()
                            alert.setCanceledOnTouchOutside(true)
                            alert.show()
                            mIncreaseMoneyViewModel.goToIncreaseDone()

                        } else {
                            mIncreaseMoneyViewModel.insertLoanPayments(
                                removeComma,
                                true,
                                numbers.toString(),
                                userId
                            )
                            mIncreaseMoneyViewModel.goToIncreaseDone()
                        }
                        mIncreaseMoneyViewModel.goToIncreaseDone()

                    })
                } else {
                    mIncreaseMoneyViewModel.insertMoney(
                        removeComma,
                        false,
                        userId
                    )
                }
                mIncreaseMoneyViewModel.increaseMoneyDone()
            }
            mIncreaseMoneyViewModel.goToIncreaseDone()

            mIncreaseMoneyViewModel.goToIncreaseDone()
        })
        mIncreaseMoneyViewModel.gotToDecreaseMoney.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                this.findNavController().navigate(
                    IncreaseMoneyFragmentDirections.actionIncreaseMoneyFragmentToDecreaseMoneyFragment(
                        userId
                    )
                )
                mIncreaseMoneyViewModel.goToIncreaseDone()
            }
        })
        mIncreaseMoneyViewModel.setIncrease(userId)?.observe(viewLifecycleOwner, {
            if (it != null) {
                mIncreaseMoneyViewModel.setIncrease(it)
            }
        })
        mIncreaseMoneyViewModel.increase.observe(viewLifecycleOwner, {
            if (it != null) {
                val formatter: NumberFormat = DecimalFormat("#,###,###,###")
                mIncreaseMoneyBinding.userMoney.setText("" + formatter.format(increase))
                if (mIncreaseMoneyBinding.totalMoney.text == "0") {
                    mIncreaseMoneyBinding.userMoney.text = it.increase
                }
                mIncreaseMoneyBinding.userMoney.text =
                    mIncreaseMoneyViewModel.sumUserIncrease(userId).toString()
                //                mIncreaseMoneyBinding.userMoney.text = it.increase.toString() + it.increase.toString()
            }
        })
        setHasOptionsMenu(true)
        mIncreaseMoneyViewModel.setLoanDetail(userId)?.observe(viewLifecycleOwner, {
            if (it?.userId != null) {
                mIncreaseMoneyBinding.checkBox?.setOnCheckedChangeListener { buttonView, isChecked ->
                    id2 = isChecked
                    if (id2 == isChecked) {
                        mIncreaseMoneyViewModel.setLoanDetail(userId)?.observe(viewLifecycleOwner, {
                            if (mIncreaseMoneyBinding.increaseEdt.text.toString() != it.payment) {
                                mIncreaseMoneyBinding.noLoanForUser.visibility = View.VISIBLE
                                val textLoanPayments = it.payment.toInt()
                                mIncreaseMoneyBinding.textPlus.visibility = View.VISIBLE
                                mIncreaseMoneyBinding.noLoanForUser.text =
                                    NumberFormat.getInstance()
                                        .format(textLoanPayments)
                            }
                        })
                    }
                }
            } else {
                mIncreaseMoneyBinding.checkBox.isEnabled = false
                mIncreaseMoneyBinding.noLoanForUser.visibility = View.VISIBLE

            }
        })
        mIncreaseMoneyBinding.increaseEdt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                try {
                    mIncreaseMoneyBinding.increaseEdt.removeTextChangedListener(this)
                    val value: String = mIncreaseMoneyBinding.increaseEdt.getText().toString()
                    if (value != "") {
                        if (value.startsWith(".")) {
                            mIncreaseMoneyBinding.increaseEdt.setText("0.")
                        }
                        if (value.startsWith("0") && !value.startsWith("0.")) {
                            mIncreaseMoneyBinding.increaseEdt.setText("")
                        }
                        val str: String = mIncreaseMoneyBinding.increaseEdt.text.toString().replace(",", "")
                        if (value != "") mIncreaseMoneyBinding.increaseEdt.setText(toCurrencyFormat(str))
                        mIncreaseMoneyBinding.increaseEdt.setSelection(mIncreaseMoneyBinding.increaseEdt.text.toString().length)
                    }
                    mIncreaseMoneyBinding.increaseEdt.addTextChangedListener(this)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    mIncreaseMoneyBinding.increaseEdt.addTextChangedListener(this)
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        return mIncreaseMoneyBinding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.loan_menu, menu);
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.transactionHistory -> {
                this.findNavController().navigate(
                    IncreaseMoneyFragmentDirections.actionIncreaseMoneyFragmentToIncreaseHistoryFragment(
                        userId
                    )
                )
//                Toast.makeText(activity, "تاریخچه تراکنش ها", Toast.LENGTH_LONG).show()
                mIncreaseMoneyViewModel.goToIncreaseDone()

                true
            }
            R.id.getLoan -> {
                this.findNavController().navigate(
                    IncreaseMoneyFragmentDirections.actionIncreaseMoneyFragmentToGetLoanFragment(
                        userId
                    )
                )
                mIncreaseMoneyViewModel.goToIncreaseDone()
                true
            }
            R.id.loanHistory -> {
                this.findNavController().navigate(
                    IncreaseMoneyFragmentDirections.actionIncreaseMoneyFragmentToLoanHistoryFragment(
                        userId
                    )
                )
                mIncreaseMoneyViewModel.goToIncreaseDone()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}