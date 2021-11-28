FROM postgres:14.1
COPY ./create.sql /docker-entrypoint-initdb.d/
ENV POSTGRES_USER postgres
ENV POSTGRES_PASSWORD postgres
ENV POSTGRES_DB biointerface
ENV PGDATA /var/lib/postgresql/data/biointerface
EXPOSE 5432
#CMD "psql -U postgres -d biointerface -a -f ./create.sql"