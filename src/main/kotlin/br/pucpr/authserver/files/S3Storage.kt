package br.pucpr.authserver.files

import br.pucpr.authserver.users.User
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.transfer.TransferManagerBuilder
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

@Component
class S3Storage: IFileStorage {
    private val s3: AmazonS3 = AmazonS3ClientBuilder.standard()
        .withRegion(Regions.US_EAST_2)
        .withCredentials(EnvironmentVariableCredentialsProvider())
        .build()

    companion object {
        const val PUBLIC = "gabrielkozuki-authserver-public"
        const val PREFIX = "https://gabrielkozuki-authserver-public.s3.us-east-2.amazonaws.com"
    }

    override fun save(
        user: User,
        path: String,
        file: MultipartFile
    ) {
        val contentType = file.contentType ?: "application/octet-stream"

        val meta = ObjectMetadata()
        meta.contentType = contentType
        meta.contentLength = file.size
        meta.userMetadata["userId"] = user.id.toString()
        meta.userMetadata["originalFileName"] = file.originalFilename

        val transferManager = TransferManagerBuilder.standard()
            .withS3Client(s3)
            .build()

        transferManager
            .upload(PUBLIC, path, file.inputStream, meta)
            .waitForUploadResult()
    }

    override fun load(path: String): Resource = InputStreamResource(
        s3.getObject(PUBLIC, path.replace("---", "/"))
            .objectContent
    )

    override fun urlFor(name: String): String = "$PREFIX/$name"

}