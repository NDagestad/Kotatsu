package org.koitharu.kotatsu.scrobbling

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ElementsIntoSet
import okhttp3.OkHttpClient
import org.koitharu.kotatsu.BuildConfig
import org.koitharu.kotatsu.core.db.MangaDatabase
import org.koitharu.kotatsu.core.network.CurlLoggingInterceptor
import org.koitharu.kotatsu.scrobbling.anilist.data.AniListAuthenticator
import org.koitharu.kotatsu.scrobbling.anilist.data.AniListInterceptor
import org.koitharu.kotatsu.scrobbling.anilist.data.AniListRepository
import org.koitharu.kotatsu.scrobbling.anilist.domain.AniListScrobbler
import org.koitharu.kotatsu.scrobbling.common.data.ScrobblerStorage
import org.koitharu.kotatsu.scrobbling.common.domain.Scrobbler
import org.koitharu.kotatsu.scrobbling.common.domain.model.ScrobblerService
import org.koitharu.kotatsu.scrobbling.common.domain.model.ScrobblerType
import org.koitharu.kotatsu.scrobbling.kitsu.data.KitsuAuthenticator
import org.koitharu.kotatsu.scrobbling.kitsu.data.KitsuInterceptor
import org.koitharu.kotatsu.scrobbling.kitsu.data.KitsuRepository
import org.koitharu.kotatsu.scrobbling.kitsu.domain.KitsuScrobbler
import org.koitharu.kotatsu.scrobbling.mal.data.MALAuthenticator
import org.koitharu.kotatsu.scrobbling.mal.data.MALInterceptor
import org.koitharu.kotatsu.scrobbling.mal.data.MALRepository
import org.koitharu.kotatsu.scrobbling.mal.domain.MALScrobbler
import org.koitharu.kotatsu.scrobbling.shikimori.data.ShikimoriAuthenticator
import org.koitharu.kotatsu.scrobbling.shikimori.data.ShikimoriInterceptor
import org.koitharu.kotatsu.scrobbling.shikimori.data.ShikimoriRepository
import org.koitharu.kotatsu.scrobbling.shikimori.domain.ShikimoriScrobbler
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ScrobblingModule {

	@Provides
	@Singleton
	fun provideShikimoriRepository(
		@ApplicationContext context: Context,
		@ScrobblerType(ScrobblerService.SHIKIMORI) storage: ScrobblerStorage,
		database: MangaDatabase,
		authenticator: ShikimoriAuthenticator,
	): ShikimoriRepository {
		val okHttp = OkHttpClient.Builder().apply {
			authenticator(authenticator)
			addInterceptor(ShikimoriInterceptor(storage))
			if (BuildConfig.DEBUG) {
				addInterceptor(CurlLoggingInterceptor())
			}
		}.build()
		return ShikimoriRepository(context, okHttp, storage, database)
	}

	@Provides
	@Singleton
	fun provideMALRepository(
		@ApplicationContext context: Context,
		@ScrobblerType(ScrobblerService.MAL) storage: ScrobblerStorage,
		database: MangaDatabase,
		authenticator: MALAuthenticator,
	): MALRepository {
		val okHttp = OkHttpClient.Builder().apply {
			authenticator(authenticator)
			addInterceptor(MALInterceptor(storage))
			if (BuildConfig.DEBUG) {
				addInterceptor(CurlLoggingInterceptor())
			}
		}.build()
		return MALRepository(context, okHttp, storage, database)
	}

	@Provides
	@Singleton
	fun provideAniListRepository(
		@ApplicationContext context: Context,
		@ScrobblerType(ScrobblerService.ANILIST) storage: ScrobblerStorage,
		database: MangaDatabase,
		authenticator: AniListAuthenticator,
	): AniListRepository {
		val okHttp = OkHttpClient.Builder().apply {
			authenticator(authenticator)
			addInterceptor(AniListInterceptor(storage))
			if (BuildConfig.DEBUG) {
				addInterceptor(CurlLoggingInterceptor())
			}
		}.build()
		return AniListRepository(context, okHttp, storage, database)
	}

	@Provides
	@Singleton
	fun provideKitsuRepository(
		@ApplicationContext context: Context,
		@ScrobblerType(ScrobblerService.KITSU) storage: ScrobblerStorage,
		database: MangaDatabase,
		authenticator: KitsuAuthenticator,
	): KitsuRepository {
		val okHttp = OkHttpClient.Builder().apply {
			authenticator(authenticator)
			addInterceptor(KitsuInterceptor(storage))
			if (BuildConfig.DEBUG) {
				addInterceptor(CurlLoggingInterceptor())
			}
		}.build()
		return KitsuRepository(context, okHttp, storage, database)
	}

	@Provides
	@Singleton
	@ScrobblerType(ScrobblerService.ANILIST)
	fun provideAniListStorage(
		@ApplicationContext context: Context,
	): ScrobblerStorage = ScrobblerStorage(context, ScrobblerService.ANILIST)

	@Provides
	@Singleton
	@ScrobblerType(ScrobblerService.SHIKIMORI)
	fun provideShikimoriStorage(
		@ApplicationContext context: Context,
	): ScrobblerStorage = ScrobblerStorage(context, ScrobblerService.SHIKIMORI)

	@Provides
	@Singleton
	@ScrobblerType(ScrobblerService.MAL)
	fun provideMALStorage(
		@ApplicationContext context: Context,
	): ScrobblerStorage = ScrobblerStorage(context, ScrobblerService.MAL)

	@Provides
	@Singleton
	@ScrobblerType(ScrobblerService.KITSU)
	fun provideKitsuStorage(
		@ApplicationContext context: Context,
	): ScrobblerStorage = ScrobblerStorage(context, ScrobblerService.KITSU)

	@Provides
	@ElementsIntoSet
	fun provideScrobblers(
		shikimoriScrobbler: ShikimoriScrobbler,
		aniListScrobbler: AniListScrobbler,
		malScrobbler: MALScrobbler,
		kitsuScrobbler: KitsuScrobbler
	): Set<@JvmSuppressWildcards Scrobbler> = setOf(shikimoriScrobbler, aniListScrobbler, malScrobbler, kitsuScrobbler)
}
