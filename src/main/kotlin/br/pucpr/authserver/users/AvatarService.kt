package br.pucpr.authserver.users

import br.pucpr.authserver.exceptions.UnsupportedMediaTypeException
import br.pucpr.authserver.files.IFileStorage
import br.pucpr.authserver.files.S3Storage
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class AvatarService(
    @Qualifier("fileStorage") private val storage: IFileStorage
) {
    fun save(user: User, avatar: MultipartFile): String {
        try {
            val extension = when(avatar.contentType) {
                "image/jpeg" -> "jpg"
                "image/jpg" -> "jpg"
                "image/png" -> "png"
                else -> throw UnsupportedMediaTypeException("jpg", "png")
            }

            val path = "$ROOT/${user.id}/a_${user.id}.$extension"
            storage.save(user, path, avatar)
            return path
        } catch (exception: Error) {
            log.warn("Could not save user ${user.id} avatar ${avatar.originalFilename}: ${exception.message}")
            return DEFAULT_AVATAR
        }
    }

    fun urlFor(path: String) = storage.urlFor(path)

    companion object {
        const val ROOT = "avatars"
        const val DEFAULT_AVATAR = "default.jpg"
        private val log = LoggerFactory.getLogger(AvatarService::class.java)
    }
}