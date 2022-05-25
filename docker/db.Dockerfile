FROM postgres:14.1
ENV POSTGRES_USER postgres
ENV POSTGRES_PASSWORD postgres
ENV POSTGRES_DB biointerface
ENV PGDATA /var/lib/postgresql/data/biointerface
EXPOSE 5432