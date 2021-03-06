package org.mifos.mobilebanking.presenters;

import android.content.Context;

import org.mifos.mobilebanking.R;
import org.mifos.mobilebanking.api.DataManager;
import org.mifos.mobilebanking.injection.ApplicationContext;
import org.mifos.mobilebanking.models.beneficary.Beneficiary;
import org.mifos.mobilebanking.presenters.base.BasePresenter;
import org.mifos.mobilebanking.ui.views.BeneficiariesView;

import java.util.List;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by dilpreet on 14/6/17.
 */

public class BeneficiaryListPresenter extends BasePresenter<BeneficiariesView> {

    private DataManager dataManager;
    private CompositeSubscription subscription;

    /**
     * Initialises the LoginPresenter by automatically injecting an instance of
     * {@link DataManager} and {@link Context}.
     *
     * @param dataManager DataManager class that provides access to the data
     *                    via the API.
     * @param context     Context of the view attached to the presenter. In this case
     *                    it is that of an {@link android.support.v7.app.AppCompatActivity}
     */
    @Inject
    public BeneficiaryListPresenter(DataManager dataManager, @ApplicationContext Context context) {
        super(context);
        this.dataManager = dataManager;
        subscription = new CompositeSubscription();
    }

    @Override
    public void detachView() {
        super.detachView();
        subscription.clear();
    }

    /**
     * Used to load Beneficiaries as a {@link List} of {@link Beneficiary} from server and notifies
     * the view to display it. And in case of any error during fetching the required details it
     * notifies the view.
     */
    public void loadBeneficiaries() {
        checkViewAttached();
        getMvpView().showProgress();
        subscription.add(dataManager.getBeneficiaryList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<Beneficiary>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().hideProgress();
                        getMvpView().showError(context
                                .getString(R.string.error_fetching_beneficiaries));
                    }

                    @Override
                    public void onNext(List<Beneficiary> beneficiaries) {
                        getMvpView().hideProgress();
                        getMvpView().showBeneficiaryList(beneficiaries);
                    }
                }));
    }
}
