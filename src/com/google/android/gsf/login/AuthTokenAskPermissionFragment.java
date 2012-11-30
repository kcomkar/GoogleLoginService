package com.google.android.gsf.login;

import android.accounts.AccountManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AuthTokenAskPermissionFragment extends LoginFragment {

	@SuppressWarnings("unused")
	private final static String TAG = "GoogleAskPermission";

	private TextView txt;
	private static final String highlightStart = "<font color=\"#33B5E5\">";
	private static final String highlightEnd = "</font>";

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getContainer().showAllowButton();
		getContainer().showDenyButton();
		final String mail = getContainer().getOptions().getString(
				AccountManager.KEY_ACCOUNT_NAME);
		final String service = getContainer().getOptions().getString(
				AccountManager.KEY_AUTH_TOKEN_LABEL);
		final int uid = getContainer().getOptions().getInt(
				AccountManager.KEY_CALLER_UID);

		String app = null;
		if (uid != 0) {
			PackageManager pm = getActivity().getPackageManager();
			String[] packages = pm.getPackagesForUid(uid);
			for (String pkg : packages) {
				if (pkg != null && !pkg.isEmpty()) {
					try {
						ApplicationInfo info = pm.getApplicationInfo(pkg, 0);
						if (info != null) {
							app = pm.getApplicationLabel(info).toString();
							if (app != null && !app.isEmpty()) {
								break;
							}
						}
					} catch (NameNotFoundException e) {
					}
				}
			}
			if ((app == null || app.isEmpty()) && packages.length > 0) {
				app = packages[0];
			}
		}
		if (app == null || app.isEmpty()) {
			app = getString(R.string.app_name);
		}
		txt.setText(Html.fromHtml(getString(R.string.ask_auth_token)
				.replace("$m", highlightStart + mail + highlightEnd)
				.replace("$a", highlightStart + app + highlightEnd)
				.replace("$s", highlightStart + service + highlightEnd)));
	}

	@Override
	public void onBackPressed() {
		getContainer().resultCancelled();
	}

	@Override
	public View onCreateView(final LayoutInflater inflater,
			final ViewGroup container, final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.ask_auth_token, null);
		txt = (TextView) view.findViewById(R.id.txt_info);
		return view;
	}

	@Override
	public void onNextPressed() {
		getContainer().getOptions().putBoolean(
				LoginActivity.KEY_FORCE_PERMISSION, true);
		getContainer().goAuthTokenAction();
	}

}
